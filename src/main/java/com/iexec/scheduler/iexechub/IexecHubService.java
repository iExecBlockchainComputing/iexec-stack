package com.iexec.scheduler.iexechub;


import com.iexec.common.contracts.generated.IexecHub;
import com.iexec.common.ethereum.*;
import com.iexec.common.workerpool.WorkerPoolConfig;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import static com.iexec.common.ethereum.Utils.END;


public class IexecHubService {


    private static final Logger log = LoggerFactory.getLogger(WorkerPoolService.class);
    private static final Web3jService web3jService = Web3jService.getInstance();
    private static final CredentialsService credentialsService = CredentialsService.getInstance();
    private static final Configuration configuration = IexecConfigurationService.getInstance().getConfiguration();
    private static final Web3jConfig web3jConfig = configuration.getWeb3jConfig();
    private static final ContractConfig contractConfig = configuration.getContractConfig();
    private static final WorkerPoolConfig workerPoolConfig = configuration.getWorkerPoolConfig();
    private static IexecHubService instance;
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
        this.iexecHub = IexecHub.load(
                contractConfig.getIexecHubAddress(), web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        log.info("Load contract IexecHub [address:{}] ", contractConfig.getIexecHubAddress());
        workerPoolConfig.setAddress(fetchWorkerPoolAddress());
        startWatchers();
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
        this.iexecHub.workOrderActivatedEventObservable(web3jConfig.getStartBlockParameter(), END)
                .subscribe(this::onWorkOrderActivated);
        this.iexecHub.workerPoolSubscriptionEventObservable(web3jConfig.getStartBlockParameter(), END)
                .subscribe(this::onSubscription);
        this.iexecHub.rewardEventObservable(web3jConfig.getStartBlockParameter(), END).subscribe(rewardEvent -> {
            if (rewardEvent.user.equals(credentialsService.getCredentials().getAddress())) {
                log.info("Received RewardEvent [amount:{}]", rewardEvent.amount);
            }
        });
        this.iexecHub.seizeEventObservable(web3jConfig.getStartBlockParameter(), END).subscribe(seizeEvent -> {
            if (seizeEvent.user.equals(credentialsService.getCredentials().getAddress())) {
                log.info("Received SeizeEvent [amount:{}]", seizeEvent.amount);
            }
        });
    }

    private void onSubscription(IexecHub.WorkerPoolSubscriptionEventResponse workerPoolSubscriptionEvent) {
        if (workerPoolSubscriptionEvent.workerPool.equals(workerPoolConfig.getAddress())) {
            log.info("Received WorkerPoolSubscriptionEvent [worker:{}]", workerPoolSubscriptionEvent.worker);
            iexecHubWatcher.onSubscription(workerPoolSubscriptionEvent.worker);
        }
    }

    private void onWorkOrderActivated(IexecHub.WorkOrderActivatedEventResponse workOrderActivatedEvent) {
        log.info("Received WorkOrderActivatedEvent [woid:{}]", workOrderActivatedEvent.woid);
        if (workOrderActivatedEvent.workerPool.equals(workerPoolConfig.getAddress())) {
            iexecHubWatcher.onWorkOrderActivated(workOrderActivatedEvent.woid);
        }
    }

    public void registerIexecHubWatcher(IexecHubWatcher iexecHubWatcher) {
        this.iexecHubWatcher = iexecHubWatcher;
    }

    public IexecHub getIexecHub() {
        return iexecHub;
    }

}
