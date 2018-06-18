package com.iexec.scheduler.iexechub;


import com.iexec.common.contracts.generated.IexecHub;
import com.iexec.common.ethereum.*;
import com.iexec.common.model.ContributionModel;
import com.iexec.common.model.StateHistoryModel;
import com.iexec.common.workerpool.WorkerPoolConfig;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.ens.EnsResolutionException;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.io.IOException;

import static com.iexec.common.ethereum.Utils.END;
import static com.iexec.common.ethereum.Utils.tuple2ContributionModel;
import static com.iexec.common.ethereum.Utils.tuple2StateHistoryModel;


public class IexecHubService {


    private static final Logger log = LoggerFactory.getLogger(WorkerPoolService.class);
    private static final Web3jService web3jService = Web3jService.getInstance();
    private static final CredentialsService credentialsService = CredentialsService.getInstance();
    private static IexecHubService instance;
    private final CommonConfiguration configuration = IexecConfigurationService.getInstance().getCommonConfiguration();
    private final NodeConfig nodeConfig = configuration.getNodeConfig();
    private final ContractConfig contractConfig = configuration.getContractConfig();
    private final WorkerPoolConfig workerPoolConfig = contractConfig.getWorkerPoolConfig();
    private IexecHub iexecHub;
    private IexecHubWatcher iexecHubWatcher;

    private IexecHubService() {
        run();
    }

    public static IexecHubService getInstance() {
        if (instance == null) {
            instance = new IexecHubService();
        }
        return instance;
    }

    private void run() {
        String iexecHubAddress = contractConfig.getIexecHubAddress();
        ExceptionInInitializerError exceptionInInitializerError = new ExceptionInInitializerError("Failed to load IexecHub contract from address " + iexecHubAddress);
        if(iexecHubAddress != null && !iexecHubAddress.isEmpty()) {
            try {
                this.iexecHub = IexecHub.load(
                        iexecHubAddress, web3jService.getWeb3j(), credentialsService.getCredentials(), configuration.getNodeConfig().getGasPrice(), configuration.getNodeConfig().getGasLimit());
                //if (!iexecHub.isValid()){ throw exceptionInInitializerError;}
                log.info("Load contract IexecHub [address:{}] ", iexecHubAddress);
            } catch (EnsResolutionException e){
                throw exceptionInInitializerError;
            }
            workerPoolConfig.setAddress(fetchWorkerPoolAddress());
            startWatchers();
        } else {
            throw exceptionInInitializerError;
        }
    }

    private String fetchWorkerPoolAddress() {
        String workerPoolAddress = null;
        if (workerPoolConfig.getAddress() == null || workerPoolConfig.getAddress().isEmpty()) {
            try {
                TransactionReceipt createWorkerPoolReceipt = iexecHub.createWorkerPool(workerPoolConfig.getName(),
                        workerPoolConfig.getSubscriptionLockStakePolicy(),
                        workerPoolConfig.getSubscriptionMinimumStakePolicy(),
                        workerPoolConfig.getSubscriptionMinimumScorePolicy()).send();
                workerPoolAddress = this.iexecHub.getCreateWorkerPoolEvents(createWorkerPoolReceipt).get(0).workerPool;
                log.info("CreateWorkerPool [address:{}] ", workerPoolAddress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            workerPoolAddress = workerPoolConfig.getAddress();
            log.info("Get WorkerPool address from configuration [address:{}] ", workerPoolAddress);
        }
        return workerPoolAddress;
    }

    private void startWatchers() {
        this.iexecHub.workOrderActivatedEventObservable(nodeConfig.getStartBlockParameter(), END)
                .subscribe(this::onWorkOrderActivated);
        this.iexecHub.workerPoolSubscriptionEventObservable(nodeConfig.getStartBlockParameter(), END)
                .subscribe(this::onSubscription);
        this.iexecHub.rewardEventObservable(nodeConfig.getStartBlockParameter(), END).subscribe(rewardEvent -> {
            if (rewardEvent.user.equals(credentialsService.getCredentials().getAddress())) {
                log.info("Received RewardEvent [amount:{}]", rewardEvent.amount);
            }
        });
        this.iexecHub.seizeEventObservable(nodeConfig.getStartBlockParameter(), END).subscribe(seizeEvent -> {
            if (seizeEvent.user.equals(credentialsService.getCredentials().getAddress())) {
                log.info("Received SeizeEvent [amount:{}]", seizeEvent.amount);
            }
        });
    }

    private void onSubscription(IexecHub.WorkerPoolSubscriptionEventResponse workerPoolSubscriptionEvent) {
        if (workerPoolSubscriptionEvent.workerPool.equals(workerPoolConfig.getAddress()) && iexecHubWatcher != null) {
            log.info("Received WorkerPoolSubscriptionEvent [worker:{}]", workerPoolSubscriptionEvent.worker);
            iexecHubWatcher.onSubscription(workerPoolSubscriptionEvent.worker);
        }
    }

    private void onWorkOrderActivated(IexecHub.WorkOrderActivatedEventResponse workOrderActivatedEvent) {
        if (workOrderActivatedEvent.workerPool.equals(workerPoolConfig.getAddress()) && iexecHubWatcher != null) {
            log.info("Received WorkOrderActivatedEvent [woid:{}]", workOrderActivatedEvent.woid);
            iexecHubWatcher.onWorkOrderActivated(workOrderActivatedEvent.woid);
        }
    }

    public void registerIexecHubWatcher(IexecHubWatcher iexecHubWatcher) {
        this.iexecHubWatcher = iexecHubWatcher;
    }

    public IexecHub getIexecHub() {
        return iexecHub;
    }

    public StateHistoryModel getContributionHistory() {
        StateHistoryModel stateHistoryModel = null;
        TransactionStatus transactionStatus = TransactionStatus.SUCCESS;
        try {
            stateHistoryModel = tuple2StateHistoryModel(iexecHub.m_contributionHistory().send());
        } catch (Exception e) {
            transactionStatus = TransactionStatus.FAILURE;
        }
        log.info("GetStateHistoryModel [transactionStatus:{}] ", transactionStatus);
        return stateHistoryModel;
    }

}
