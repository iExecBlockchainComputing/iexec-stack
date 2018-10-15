#!/usr/bin/env node

const Debug = require('debug');
const cli = require('commander');
const createIExecClient = require('iexec-server-js-client');
const path = require('path');
const fs = require('fs');
const {
  help,
  handleError,
  desc,
  option,
  Spinner,
  info,
} = require('./cli-helper');
const keystore = require('./keystore');
const { loadDeployedObj, loadAccountConf } = require('./fs');
const { loadChain } = require('./chains.js');
const { decodeJWTForPrint } = require('./utils');
const work = require('./work');

const debug = Debug('iexec:iexec-work');
const objName = 'work';

cli
  .command('show [address]')
  .option(...option.chain())
  .option(...option.watch())
  .option(...option.download())
  .description(desc.showObj(objName))
  .action(async (address, cmd) => {
    const spinner = Spinner();
    try {
      const [chain, deployedObj, { jwtoken }, userWallet] = await Promise.all([
        loadChain(cmd.chain),
        loadDeployedObj(objName),
        loadAccountConf(),
        keystore.load(),
      ]);
      debug('cmd.watch', cmd.watch);
      debug('cmd.download', cmd.download);

      const objAddress = address || deployedObj[chain.id];

      if (!objAddress) throw Error(info.missingAddress(objName));

      const workResult = await work.show(chain.contracts, objAddress, cmd);

      if (cmd.download) {
        const jwtForPrint = decodeJWTForPrint(jwtoken);

        if (
          userWallet.address.toLowerCase() !== jwtForPrint.address.toLowerCase()
        ) {
          spinner.warn(
            info.tokenAndWalletDiffer(userWallet.address, jwtForPrint.address),
          );
        }

        if (workResult.m_statusName === 'COMPLETED') {
          const server = 'https://'.concat(workResult.m_uri.split('/')[2]);
          debug('server', server);
          const scheduler = createIExecClient({ server });
          await scheduler.getCookieByJWT(jwtoken);

          const resultUID = scheduler.uri2uid(workResult.m_uri);
          debug('resultUID', resultUID);
          const resultObj = await scheduler.getByUID(resultUID);
          const extension = scheduler
            .getFieldValue(resultObj, 'type')
            .toLowerCase();

          const fileName = typeof cmd.download === 'string' ? cmd.download : objAddress;
          const resultPath = path.join(
            process.cwd(),
            fileName.concat('.', extension),
          );
          const resultStream = fs.createWriteStream(resultPath);
          await scheduler.downloadStream(resultUID, resultStream);
          spinner.succeed(info.downloaded(resultPath));
        } else {
          spinner.info(
            '--download option ignored because work status is not "COMPLETED"',
          );
        }
      }
      if (['ACTIVE', 'REVEALING'].includes(workResult.m_statusName)) {
        const workerPoolContract = await chain.contracts.getWorkerPoolContract({
          at: workResult.m_workerpool,
        });
        const consensuDetails = await workerPoolContract.getConsensusDetails(
          objAddress,
        );

        const consensusTimeout = consensuDetails.c_consensusTimeout.toNumber();
        const consensusTimeoutDate = new Date(consensusTimeout * 1000);

        const now = Math.floor(Date.now() / 1000);
        if (now < consensusTimeout) {
          spinner.info(
            `if work is not "COMPLETED" after ${consensusTimeoutDate} you can claim the work to get a full refund using "iexec work claim"`,
          );
        } else {
          spinner.info(
            `consensus timeout date ${consensusTimeoutDate} exceeded but consensus not reached. You can claim the work to get a full refund using "iexec work claim"`,
          );
        }
      }
    } catch (error) {
      handleError(error, cli);
    }
  });

cli
  .command('claim [address]')
  .option(...option.chain())
  .option(...option.hub())
  .description(desc.claimObj(objName))
  .action(async (address, cmd) => {
    try {
      const [chain, deployedObj, wallet] = await Promise.all([
        loadChain(cmd.chain),
        loadDeployedObj(objName),
        keystore.load(),
      ]);
      const hubAddress = cmd.hub || chain.hub;
      const objAddress = address || deployedObj[chain.id];

      if (!objAddress) throw Error(info.missingAddress(objName));

      await work.claim(chain.contracts, objAddress, wallet.address, {
        hub: hubAddress,
      });
    } catch (error) {
      handleError(error, cli);
    }
  });

help(cli);
