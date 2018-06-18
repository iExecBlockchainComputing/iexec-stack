#!/usr/bin/env node

const cli = require('commander');
const {
  help,
  handleError,
  desc,
  option,
  Spinner,
  pretty,
  info,
} = require('./cli-helper');
const hub = require('./hub');
const {
  loadIExecConf,
  initObj,
  saveDeployedObj,
  loadDeployedObj,
} = require('./fs');
const { load } = require('./keystore');
const { loadChain } = require('./chains.js');

const objName = 'dataset';

cli
  .command('init')
  .description(desc.initObj(objName))
  .action(async () => {
    const spinner = Spinner();
    try {
      const { saved, fileName } = await initObj(objName);
      spinner.succeed(`Saved default ${objName} in "${fileName}", you can edit it:${pretty(saved)}`);
    } catch (error) {
      handleError(error, cli);
    }
  });

cli
  .command('deploy')
  .option(...option.chain())
  .option(...option.hub())
  .description(desc.deployObj(objName))
  .action(async (cmd) => {
    try {
      const [chain, iexecConf] = await Promise.all([
        loadChain(cmd.chain),
        loadIExecConf(),
      ]);
      const hubAddress = cmd.hub || chain.hub;
      const events = await hub.createObj(objName)(
        chain.contracts,
        iexecConf[objName],
        {
          hub: hubAddress,
        },
      );
      await saveDeployedObj(objName, chain.id, events[0][objName]);
    } catch (error) {
      handleError(error, cli);
    }
  });

cli
  .command('show [addressOrIndex]')
  .option(...option.chain())
  .option(...option.hub())
  .option(...option.user())
  .description(desc.showObj(objName))
  .action(async (cliAddressOrIndex, cmd) => {
    try {
      const [chain, { address }, deployedObj] = await Promise.all([
        loadChain(cmd.chain),
        load(),
        loadDeployedObj(objName),
      ]);

      const hubAddress = cmd.hub || chain.hub;
      const userAddress = cmd.user || address;
      const addressOrIndex = cliAddressOrIndex || deployedObj[chain.id];

      if (!addressOrIndex) throw Error(info.missingAddress(objName));

      await hub.showObj(objName)(chain.contracts, addressOrIndex, userAddress, {
        hub: hubAddress,
      });
    } catch (error) {
      handleError(error, cli);
    }
  });

cli
  .command('count')
  .option(...option.chain())
  .option(...option.hub())
  .option(...option.user())
  .description(desc.countObj(objName))
  .action(async (cmd) => {
    try {
      const [chain, { address }] = await Promise.all([
        loadChain(cmd.chain),
        load(),
      ]);
      const hubAddress = cmd.hub || chain.hub;
      const userAddress = cmd.user || address;

      await hub.countObj(objName)(chain.contracts, userAddress, {
        hub: hubAddress,
      });
    } catch (error) {
      handleError(error, cli);
    }
  });

help(cli);
