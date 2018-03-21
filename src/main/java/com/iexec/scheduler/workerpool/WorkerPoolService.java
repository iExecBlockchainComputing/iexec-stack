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
        log.debug("SCHEDLR loading workerPool " + poolConfig.getAddress());
        this.workerPool = WorkerPool.load(
                poolConfig.getAddress(), web3j, credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
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
        log.debug("SCHEDLR pool policy needs changes?");
        if (!poolConfig.getStakeRatioPolicy().equals(workerPool.m_stakeRatioPolicy().send()) ||
                !poolConfig.getSchedulerRewardRatioPolicy().equals(workerPool.m_schedulerRewardRatioPolicy().send()) ||
                !poolConfig.getSubscriptionMinimumStakePolicy().equals(workerPool.m_subscriptionMinimumStakePolicy().send()) ||
                !poolConfig.getSubscriptionMinimumScorePolicy().equals(workerPool.m_subscriptionMinimumScorePolicy().send())) {

            workerPool.changeWorkerPoolPolicy(poolConfig.getStakeRatioPolicy(),
                    poolConfig.getSchedulerRewardRatioPolicy(),
                    poolConfig.getResultRetentionPolicy(),
                    poolConfig.getSubscriptionMinimumStakePolicy(),
                    poolConfig.getSubscriptionMinimumScorePolicy()).send();
            log.debug("SCHEDLR yes");
        } else {
            log.debug("SCHEDLR no");
        }
    }

    private void updateAuthorizedList(AuthorizedList workerAuthorizedList) throws Exception {
        if (!poolConfig.getMode().equals(workerAuthorizedList.m_policy().send())) {
            workerAuthorizedList.changeListPolicy(poolConfig.getMode()).send();
        }

        //TODO - Make possible to unblacklist an ex-blacklisted worker (and do the same for whitelisting feature)
        log.debug("SCHEDLR authorizedList needs changes?");
        for (String worker : poolConfig.getList()) {//update hole list if one worker is not whitelisted
            if (poolConfig.getMode().equals(PolicyEnum.WHITELIST) && !workerAuthorizedList.isWhitelisted(worker).send()) {
                workerAuthorizedList.updateWhitelist(poolConfig.getList(), true).send();
                log.debug("SCHEDLR yes");
                return;
            } else if (poolConfig.getMode().equals(PolicyEnum.BLACKLIST) && !workerAuthorizedList.isblacklisted(worker).send()) {
                workerAuthorizedList.updateBlacklist(poolConfig.getList(), true).send();
                log.debug("SCHEDLR yes");
                return;
            }
        }
        log.debug("SCHEDLR no");
    }

    private void watchWorkerPoolPolicy(WorkerPool workerPool) {
        log.debug("SCHEDLR watching WorkerPoolPolicyUpdateEvent");
        workerPool.workerPoolPolicyUpdateEventObservable(ethConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST)
                .subscribe(workerPoolPolicyUpdateEvent -> {
                    log.debug("SCHEDLR received WorkerPoolPolicyUpdateEvent (newSchedulerRewardRatioPolicy,..)");
                });
    }

    private void watchPolicyChange(AuthorizedList authorizedList) {
        log.debug("SCHEDLR watching PolicyChangeEvent (black/whitelist)");
        authorizedList.policyChangeEventObservable(ethConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST)
                .subscribe(policyChangeEvent -> {
                    log.debug("SCHEDLR received policyChangeEvent (black/whitelist) on workerpool from " + policyChangeEvent.oldPolicy + " to " + policyChangeEvent.newPolicy);
                });
    }

    private void watchBlacklistChange(AuthorizedList authorizedList) {
        log.debug("SCHEDLR watching BlacklistChangeEvent (0xbadworker,..)");
        authorizedList.blacklistChangeEventObservable(ethConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST)
                .subscribe(blacklistChangeEvent -> {
                    log.debug("SCHEDLR received BlacklistChangeEvent: " + blacklistChangeEvent.actor + " listed " + blacklistChangeEvent.isBlacklisted);
                });
    }

    private void watchWhitelistChange(AuthorizedList authorizedList) {
        log.debug("SCHEDLR watching WhitelistChangeEvent (0xgoodworker,..)");
        authorizedList.whitelistChangeEventObservable(ethConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST)
                .subscribe(whitelistChangeEvent -> {
                    log.debug("SCHEDLR received WhitelistChangeEvent: " + whitelistChangeEvent.actor + " listed " + whitelistChangeEvent.isWhitelisted);
                });
    }

    private void startWatchers() {
        log.debug("SCHEDLR watching ContributeEvent (auto revealConsensus)");
        this.workerPool.contributeEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(this::onContributeEvent);
        log.debug("SCHEDLR watching RevealEvent (auto finalizeWork)");
        this.workerPool.revealEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(this::onReveal);
    }

    private void onContributeEvent(WorkerPool.ContributeEventResponse contributeEvent) {
        log.debug("SCHEDLR received ContributeEvent " + contributeEvent.woid + " of worker " + contributeEvent.worker);
        workerPoolWatcher.onContributeEvent(contributeEvent);
    }


    private void onReveal(WorkerPool.RevealEventResponse revealEvent) {
        log.debug("SCHEDLR received RevealEvent: " + Numeric.toHexString(revealEvent.result));
        workerPoolWatcher.onReveal(revealEvent);
    }

    public void register(WorkerPoolWatcher workerPoolWatcher) {

        this.workerPoolWatcher = workerPoolWatcher;
    }

    public WorkerPool getWorkerPool() {
        return workerPool;
    }


    public WorkerPoolConfig getPoolConfig() {
        return poolConfig;
    }
}
