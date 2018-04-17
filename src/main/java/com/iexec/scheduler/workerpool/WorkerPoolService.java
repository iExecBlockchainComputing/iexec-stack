package com.iexec.scheduler.workerpool;

import com.iexec.common.contracts.generated.IexecHub;
import com.iexec.common.contracts.generated.WorkerPool;
import com.iexec.common.ethereum.*;
import com.iexec.common.workerpool.WorkerPoolConfig;
import com.iexec.scheduler.iexechub.IexecHubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.ens.EnsResolutionException;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.utils.Numeric;

import static com.iexec.common.ethereum.Utils.END;


public class WorkerPoolService {

    private static final Logger log = LoggerFactory.getLogger(WorkerPoolService.class);
    private static WorkerPoolService instance;
    private final Web3jService web3jService = Web3jService.getInstance();
    private final CredentialsService credentialsService = CredentialsService.getInstance();
    private final CommonConfiguration configuration = IexecConfigurationService.getInstance().getCommonConfiguration();
    private final NodeConfig nodeConfig = configuration.getNodeConfig();
    private final ContractConfig contractConfig = configuration.getContractConfig();
    private final WorkerPoolConfig workerPoolConfig = contractConfig.getWorkerPoolConfig();
    private WorkerPool workerPool;
    private WorkerPoolWatcher workerPoolWatcher;


    private WorkerPoolService() {
        run();
    }

    public static WorkerPoolService getInstance() {
        if (instance == null) {
            instance = new WorkerPoolService();
        }
        return instance;
    }

    private void run() {
        loadWorkerPool();
        setupWorkerPool(workerPool);
        startWatchers();
    }

    private void loadWorkerPool() {
        String workerpookAddress = workerPoolConfig.getAddress();
        ExceptionInInitializerError exceptionInInitializerError = new ExceptionInInitializerError("Failed to load WorkerPool contract from address " + workerpookAddress);
        if (IexecHubService.getInstance() != null
                && workerpookAddress != null
                && !workerpookAddress.isEmpty()) {
            try {
                this.workerPool = WorkerPool.load(
                        workerpookAddress, web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                //if (!workerPool.isValid()){ throw exceptionInInitializerError;}
                log.info("Load contract WorkerPool [address:{}] ", workerpookAddress);
            } catch (EnsResolutionException e){
                throw exceptionInInitializerError;
            }
        } else {
            throw exceptionInInitializerError;
        }
    }

    private void setupWorkerPool(WorkerPool workerPool) {
        try {
            watchWorkerPoolPolicy(workerPool);
            updateWorkerPoolPolicy(workerPool);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void watchWorkerPoolPolicy(WorkerPool workerPool) {
        workerPool.workerPoolPolicyUpdateEventObservable(nodeConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST)
                .subscribe(workerPoolPolicyUpdateEvent -> {
                    log.info("Received WorkerPoolPolicyUpdateEvent");
                });
    }

    private boolean updateWorkerPoolPolicy(WorkerPool workerPool) throws Exception {
        boolean updated = false;
        if (!workerPoolConfig.getStakeRatioPolicy().equals(workerPool.m_stakeRatioPolicy().send()) ||
                !workerPoolConfig.getSchedulerRewardRatioPolicy().equals(workerPool.m_schedulerRewardRatioPolicy().send()) ||
                !workerPoolConfig.getSubscriptionMinimumStakePolicy().equals(workerPool.m_subscriptionMinimumStakePolicy().send()) ||
                !workerPoolConfig.getSubscriptionMinimumScorePolicy().equals(workerPool.m_subscriptionMinimumScorePolicy().send())) {

            workerPool.changeWorkerPoolPolicy(workerPoolConfig.getStakeRatioPolicy(),
                    workerPoolConfig.getSchedulerRewardRatioPolicy(),
                    workerPoolConfig.getSubscriptionMinimumStakePolicy(),
                    workerPoolConfig.getSubscriptionMinimumScorePolicy()).send();
            updated = true;
        }
        log.info("PoolPolicy updated [updated:{}]", updated);
        return updated;
    }

    private void startWatchers() {
        this.workerPool.contributeEventObservable(nodeConfig.getStartBlockParameter(), END)
                .subscribe(this::onContributeEvent);
        this.workerPool.revealEventObservable(nodeConfig.getStartBlockParameter(), END)
                .subscribe(this::onReveal);
    }

    private void onContributeEvent(WorkerPool.ContributeEventResponse contributeEvent) {
        log.info("Received ContributeEvent [woid:{}, worker:{}]",
                contributeEvent.woid, contributeEvent.worker);
        if (workerPoolWatcher != null) {
            workerPoolWatcher.onContributeEvent(contributeEvent);
        }
    }


    private void onReveal(WorkerPool.RevealEventResponse revealEvent) {
        log.info("Received RevealEvent [result:{}]", Numeric.toHexString(revealEvent.result));
        if (workerPoolWatcher != null) {
            workerPoolWatcher.onReveal(revealEvent);
        }
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
