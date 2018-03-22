package com.iexec.scheduler.workerpool;

import com.iexec.scheduler.contracts.generated.AuthorizedList;
import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.ethereum.CredentialsService;
import com.iexec.scheduler.ethereum.EthConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;

import static com.iexec.scheduler.ethereum.Utils.END;

@Service
public class WorkerPoolService {

    private static final Logger log = LoggerFactory.getLogger(WorkerPoolService.class);
    private final Web3j web3j;
    private final CredentialsService credentialsService;
    private final WorkerPoolConfig poolConfig;
    private final EthConfig ethConfig;
    private WorkerPool workerPool;
    private WorkerPoolWatcher workerPoolWatcher;

    @Autowired
    public WorkerPoolService(Web3j web3j, CredentialsService credentialsService,
                             WorkerPoolConfig poolConfig, EthConfig ethConfig) {
        this.web3j = web3j;
        this.credentialsService = credentialsService;
        this.poolConfig = poolConfig;
        this.ethConfig = ethConfig;
    }

    @PostConstruct
    public void run() {
        loadWorkerPool();
        setupWorkerPool(workerPool);
        startWatchers();
    }

    private void loadWorkerPool() {
        this.workerPool = WorkerPool.load(
                poolConfig.getAddress(), web3j, credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        log.info("Load contract WorkerPool [address:{}] ", poolConfig.getAddress());
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
        boolean updated = false;
        if (!poolConfig.getStakeRatioPolicy().equals(workerPool.m_stakeRatioPolicy().send()) ||
                !poolConfig.getSchedulerRewardRatioPolicy().equals(workerPool.m_schedulerRewardRatioPolicy().send()) ||
                !poolConfig.getSubscriptionMinimumStakePolicy().equals(workerPool.m_subscriptionMinimumStakePolicy().send()) ||
                !poolConfig.getSubscriptionMinimumScorePolicy().equals(workerPool.m_subscriptionMinimumScorePolicy().send())) {

            workerPool.changeWorkerPoolPolicy(poolConfig.getStakeRatioPolicy(),
                    poolConfig.getSchedulerRewardRatioPolicy(),
                    poolConfig.getResultRetentionPolicy(),
                    poolConfig.getSubscriptionMinimumStakePolicy(),
                    poolConfig.getSubscriptionMinimumScorePolicy()).send();
            updated = true;
        }
        log.info("PoolPolicy updated [updated:{}]", updated);
    }

    private void updateAuthorizedList(AuthorizedList workerAuthorizedList) throws Exception {
        if (!poolConfig.getMode().equals(workerAuthorizedList.m_policy().send())) {
            workerAuthorizedList.changeListPolicy(poolConfig.getMode()).send();
        }

        //TODO - Make possible to unblacklist an ex-blacklisted worker (and do the same for whitelisting feature)
        boolean updated = false;
        for (String worker : poolConfig.getList()) {//update hole list if one worker is not whitelisted
            if (poolConfig.getMode().equals(PolicyEnum.WHITELIST) && !workerAuthorizedList.isWhitelisted(worker).send()) {
                workerAuthorizedList.updateWhitelist(poolConfig.getList(), true).send();
                updated = true;
                break;
            } else if (poolConfig.getMode().equals(PolicyEnum.BLACKLIST) && !workerAuthorizedList.isblacklisted(worker).send()) {
                workerAuthorizedList.updateBlacklist(poolConfig.getList(), true).send();
                updated = true;
                break;
            }
        }
        log.info("WorkerAuthorizedList updated [updated:{}]", updated);
    }

    private void watchWorkerPoolPolicy(WorkerPool workerPool) {
        workerPool.workerPoolPolicyUpdateEventObservable(ethConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST)
                .subscribe(workerPoolPolicyUpdateEvent -> {
                    log.info("Received WorkerPoolPolicyUpdateEvent");
                });
    }

    private void watchPolicyChange(AuthorizedList authorizedList) {
        authorizedList.policyChangeEventObservable(ethConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST)
                .subscribe(policyChangeEvent -> {
                    log.info("Received PolicyChangeEvent on WorkerAuthorizedList [oldPolicy:{}, newPolicy:{}]",
                            policyChangeEvent.oldPolicy, policyChangeEvent.newPolicy);
                });
    }

    private void watchBlacklistChange(AuthorizedList authorizedList) {
        authorizedList.blacklistChangeEventObservable(ethConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST)
                .subscribe(blacklistChangeEvent -> {
                    log.info("Received BlacklistChangeEvent on WorkerAuthorizedList [actor:{}, isBlacklisted:{}]",
                            blacklistChangeEvent.actor, blacklistChangeEvent.isBlacklisted);
                });
    }

    private void watchWhitelistChange(AuthorizedList authorizedList) {
        authorizedList.whitelistChangeEventObservable(ethConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST)
                .subscribe(whitelistChangeEvent -> {
                    log.info("Received WhitelistChangeEvent on WorkerAuthorizedList [actor:{}, isBlacklisted:{}]",
                            whitelistChangeEvent.actor, whitelistChangeEvent.isWhitelisted);
                });
    }

    private void startWatchers() {
        this.workerPool.contributeEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(this::onContributeEvent);
        this.workerPool.revealEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(this::onReveal);
    }

    private void onContributeEvent(WorkerPool.ContributeEventResponse contributeEvent) {
        log.info("Received ContributeEvent [woid:{}, worker:{}]",
                contributeEvent.woid, contributeEvent.worker);
        workerPoolWatcher.onContributeEvent(contributeEvent);
    }


    private void onReveal(WorkerPool.RevealEventResponse revealEvent) {
        log.info("Received RevealEvent [result:{}]", Numeric.toHexString(revealEvent.result));
        workerPoolWatcher.onReveal(revealEvent);
    }

    public void registerWorkerPoolWatcher(WorkerPoolWatcher workerPoolWatcher) {
        this.workerPoolWatcher = workerPoolWatcher;
    }

    public WorkerPool getWorkerPool() {
        return workerPool;
    }


    public WorkerPoolConfig getPoolConfig() {
        return poolConfig;
    }
}
