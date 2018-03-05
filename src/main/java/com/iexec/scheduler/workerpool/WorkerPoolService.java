package com.iexec.scheduler.workerpool;

import com.iexec.scheduler.ethereum.EthConfig;
import com.iexec.scheduler.contracts.generated.AuthorizedList;
import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.ethereum.CredentialsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import javax.annotation.PostConstruct;

@Service
public class WorkerPoolService {

    private static final Logger log = LoggerFactory.getLogger(WorkerPoolService.class);
    private final Web3j web3j;
    private final CredentialsService credentialsService;
    private final WorkerPoolCreationWatcherService createWorkerPoolWatcherService;
    private final WorkerPoolConfig poolConfig;
    private final EthConfig ethConfig;
    private WorkerPool workerPool;

    @Autowired
    public WorkerPoolService(Web3j web3j, CredentialsService credentialsService,
                             WorkerPoolCreationWatcherService createWorkerPoolWatcherService,
                             WorkerPoolConfig poolConfig, EthConfig ethConfig) {
        this.web3j = web3j;
        this.credentialsService = credentialsService;
        this.createWorkerPoolWatcherService = createWorkerPoolWatcherService;
        this.poolConfig = poolConfig;
        this.ethConfig = ethConfig;
    }

    @PostConstruct
    public void run() throws Exception {
        loadWorkerPool();
        setupWorkerPool(workerPool);
    }

    private void loadWorkerPool() {
        String workerPoolAddress = poolConfig.getAddress();
        if (workerPoolAddress.isEmpty()){
            workerPoolAddress = createWorkerPoolWatcherService.getWorkerPoolAddress();
        }
        log.info("SCHEDLR loading workerPool");
        this.workerPool = WorkerPool.load(
                workerPoolAddress, web3j, credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    private void setupWorkerPool(WorkerPool workerPool) {
        try {
            String workersAuthorizedListAddress = workerPool.m_workersAuthorizedListAddress().send();
            AuthorizedList workerAuthorizedList = AuthorizedList.load(
                    workersAuthorizedListAddress, web3j, credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

            watchWorkerPoolPolicy(workerPool);
            watchPolicyChange(workerAuthorizedList);
            watchBlacklistChange(workerAuthorizedList);
            watchWhitelistChange(workerAuthorizedList);

            updateWorkerPoolPolicy(workerPool);
            updateAuthorizedList(workerAuthorizedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateWorkerPoolPolicy(WorkerPool workerPool) throws Exception {
        log.info("SCHEDLR pool policy needs changes?");
        if (!poolConfig.getStakeRatioPolicy().equals(workerPool.m_stakeRatioPolicy().send()) ||
                !poolConfig.getSchedulerRewardRatioPolicy().equals(workerPool.m_schedulerRewardRatioPolicy().send()) ||
                !poolConfig.getSubscriptionMinimumStakePolicy().equals(workerPool.m_subscriptionMinimumStakePolicy().send()) ||
                !poolConfig.getSubscriptionMinimumScorePolicy().equals(workerPool.m_subscriptionMinimumScorePolicy().send())) {

            workerPool.changeWorkerPoolPolicy(poolConfig.getStakeRatioPolicy(),
                    poolConfig.getSchedulerRewardRatioPolicy(),
                    poolConfig.getResultRetentionPolicy(),
                    poolConfig.getSubscriptionMinimumStakePolicy(),
                    poolConfig.getSubscriptionMinimumScorePolicy()).send();
            log.info("SCHEDLR yes");
        } else {
            log.info("SCHEDLR no");
        }
    }

    private void updateAuthorizedList(AuthorizedList workerAuthorizedList) throws Exception {
        if (!poolConfig.getMode().equals(workerAuthorizedList.m_policy().send())){
            workerAuthorizedList.changeListPolicy(poolConfig.getMode()).send();
        }

        //TODO - Make possible to unblacklist an ex-blacklisted worker (and do the same for whitelisting feature)
        log.info("SCHEDLR authorizedList needs changes?");
        for (String worker: poolConfig.getList()){//update hole list if one worker is not whitelisted
            if (poolConfig.getMode().equals(PolicyEnum.WHITELIST) && !workerAuthorizedList.isWhitelisted(worker).send()) {
                workerAuthorizedList.updateWhitelist(poolConfig.getList(), true).send();
                log.info("SCHEDLR yes");
                return;
            } else if (poolConfig.getMode().equals(PolicyEnum.BLACKLIST) && !workerAuthorizedList.isblacklisted(worker).send()) {
                workerAuthorizedList.updateBlacklist(poolConfig.getList(), true).send();
                log.info("SCHEDLR yes");
                return;
            }
        }
        log.info("SCHEDLR no");
    }

    private void watchWorkerPoolPolicy(WorkerPool workerPool) {
        log.info("SCHEDLR watching WorkerPoolPolicyUpdateEvent");
        workerPool.workerPoolPolicyUpdateEventObservable(ethConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST)
                .subscribe(workerPoolPolicyUpdateEvent -> {
                    log.info("SCHEDLR received WorkerPoolPolicyUpdateEvent (newSchedulerRewardRatioPolicy,..)");
                });
    }

    private void watchPolicyChange(AuthorizedList authorizedList) {
        log.info("SCHEDLR watching PolicyChangeEvent (black/whitelist)");
        authorizedList.policyChangeEventObservable(ethConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST)
                .subscribe(policyChangeEvent -> {
                    log.info("SCHEDLR received policyChangeEvent (black/whitelist) on workerpool from " + policyChangeEvent.oldPolicy + " to " + policyChangeEvent.newPolicy);
                });
    }

    private void watchBlacklistChange(AuthorizedList authorizedList) {
        log.info("SCHEDLR watching BlacklistChangeEvent (0xbadworker,..)");
        authorizedList.blacklistChangeEventObservable(ethConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST)
                .subscribe(blacklistChangeEvent -> {
                    log.info("SCHEDLR received BlacklistChangeEvent: " + blacklistChangeEvent.actor + " listed " + blacklistChangeEvent.isBlacklisted);
                });
    }

    private void watchWhitelistChange(AuthorizedList authorizedList) {
        log.info("SCHEDLR watching WhitelistChangeEvent (0xgoodworker,..)");
        authorizedList.whitelistChangeEventObservable(ethConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST)
                .subscribe(whitelistChangeEvent -> {
                    log.info("SCHEDLR received WhitelistChangeEvent: " + whitelistChangeEvent.actor + " listed " + whitelistChangeEvent.isWhitelisted);
                });
    }

    public WorkerPool getWorkerPool() {
        return workerPool;
    }

    public String getWorkerPoolAddress() {
        return workerPool.getContractAddress();
    }

}
