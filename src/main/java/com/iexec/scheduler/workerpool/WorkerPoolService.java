package com.iexec.scheduler.workerpool;

import com.iexec.scheduler.contracts.generated.AuthorizedList;
import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.ethereum.*;
import com.iexec.scheduler.iexechub.IexecHubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.utils.Numeric;

import static com.iexec.scheduler.ethereum.Utils.END;


public class WorkerPoolService {

    private static final Logger log = LoggerFactory.getLogger(WorkerPoolService.class);
    private static WorkerPoolService instance;
    private final Web3jService web3jService = Web3jService.getInstance();
    private final CredentialsService credentialsService = CredentialsService.getInstance();
    private final Configuration configuration = ConfigurationService.getInstance().getConfiguration();
    private final Web3jConfig web3jConfig  = configuration.getWeb3jConfig();
    private final ContractConfig contractConfig = configuration.getContractConfig();
    private final WorkerPoolConfig workerPoolConfig = configuration.getWorkerPoolConfig();
    private WorkerPool workerPool;
    private WorkerPoolWatcher workerPoolWatcher;


    public static WorkerPoolService getInstance() {
        if (instance==null){
            instance = new WorkerPoolService();
        }
        return instance;
    }

    private WorkerPoolService() {
        run();
    }

    private void run() {
        loadWorkerPool();
        setupWorkerPool(workerPool);
        startWatchers();
    }

    private void loadWorkerPool() {
        //TODO - change dependency here
        IexecHubService iexecHubService = IexecHubService.getInstance();
        this.workerPool = WorkerPool.load(
                workerPoolConfig.getAddress(), web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        log.info("Load contract WorkerPool [address:{}] ", workerPoolConfig.getAddress());
    }

    private void setupWorkerPool(WorkerPool workerPool) {
        try {
            String workersAuthorizedListAddress = workerPool.m_workersAuthorizedListAddress().send();
            AuthorizedList workerAuthorizedList = AuthorizedList.load(
                    workersAuthorizedListAddress, web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

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
        if (!workerPoolConfig.getStakeRatioPolicy().equals(workerPool.m_stakeRatioPolicy().send()) ||
                !workerPoolConfig.getSchedulerRewardRatioPolicy().equals(workerPool.m_schedulerRewardRatioPolicy().send()) ||
                !workerPoolConfig.getSubscriptionMinimumStakePolicy().equals(workerPool.m_subscriptionMinimumStakePolicy().send()) ||
                !workerPoolConfig.getSubscriptionMinimumScorePolicy().equals(workerPool.m_subscriptionMinimumScorePolicy().send())) {

            workerPool.changeWorkerPoolPolicy(workerPoolConfig.getStakeRatioPolicy(),
                    workerPoolConfig.getSchedulerRewardRatioPolicy(),
                    workerPoolConfig.getResultRetentionPolicy(),
                    workerPoolConfig.getSubscriptionMinimumStakePolicy(),
                    workerPoolConfig.getSubscriptionMinimumScorePolicy()).send();
            updated = true;
        }
        log.info("PoolPolicy updated [updated:{}]", updated);
    }

    private void updateAuthorizedList(AuthorizedList workerAuthorizedList) throws Exception {
        if (!workerPoolConfig.getMode().equals(workerAuthorizedList.m_policy().send())) {
            workerAuthorizedList.changeListPolicy(workerPoolConfig.getMode()).send();
        }

        //TODO - Make possible to unblacklist an ex-blacklisted worker (and do the same for whitelisting feature)
        boolean updated = false;
        for (String worker : workerPoolConfig.getList()) {//update hole list if one worker is not whitelisted
            if (workerPoolConfig.getMode().equals(PolicyEnum.WHITELIST) && !workerAuthorizedList.isWhitelisted(worker).send()) {
                workerAuthorizedList.updateWhitelist(workerPoolConfig.getList(), true).send();
                updated = true;
                break;
            } else if (workerPoolConfig.getMode().equals(PolicyEnum.BLACKLIST) && !workerAuthorizedList.isblacklisted(worker).send()) {
                workerAuthorizedList.updateBlacklist(workerPoolConfig.getList(), true).send();
                updated = true;
                break;
            }
        }
        log.info("WorkerAuthorizedList updated [updated:{}]", updated);
    }

    private void watchWorkerPoolPolicy(WorkerPool workerPool) {
        workerPool.workerPoolPolicyUpdateEventObservable(web3jConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST)
                .subscribe(workerPoolPolicyUpdateEvent -> {
                    log.info("Received WorkerPoolPolicyUpdateEvent");
                });
    }

    private void watchPolicyChange(AuthorizedList authorizedList) {
        authorizedList.policyChangeEventObservable(web3jConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST)
                .subscribe(policyChangeEvent -> {
                    log.info("Received PolicyChangeEvent on WorkerAuthorizedList [oldPolicy:{}, newPolicy:{}]",
                            policyChangeEvent.oldPolicy, policyChangeEvent.newPolicy);
                });
    }

    private void watchBlacklistChange(AuthorizedList authorizedList) {
        authorizedList.blacklistChangeEventObservable(web3jConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST)
                .subscribe(blacklistChangeEvent -> {
                    log.info("Received BlacklistChangeEvent on WorkerAuthorizedList [actor:{}, isBlacklisted:{}]",
                            blacklistChangeEvent.actor, blacklistChangeEvent.isBlacklisted);
                });
    }

    private void watchWhitelistChange(AuthorizedList authorizedList) {
        authorizedList.whitelistChangeEventObservable(web3jConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST)
                .subscribe(whitelistChangeEvent -> {
                    log.info("Received WhitelistChangeEvent on WorkerAuthorizedList [actor:{}, isBlacklisted:{}]",
                            whitelistChangeEvent.actor, whitelistChangeEvent.isWhitelisted);
                });
    }

    private void startWatchers() {
        this.workerPool.contributeEventObservable(web3jConfig.getStartBlockParameter(), END)
                .subscribe(this::onContributeEvent);
        this.workerPool.revealEventObservable(web3jConfig.getStartBlockParameter(), END)
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


    public WorkerPoolConfig getWorkerPoolConfig() {
        return workerPoolConfig;
    }
}
