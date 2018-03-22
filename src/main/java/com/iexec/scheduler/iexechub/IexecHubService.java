package com.iexec.scheduler.iexechub;

import com.iexec.scheduler.contracts.generated.IexecHub;
import com.iexec.scheduler.ethereum.CredentialsService;
import com.iexec.scheduler.ethereum.EthConfig;
import com.iexec.scheduler.workerpool.WorkerPoolConfig;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import javax.annotation.PostConstruct;

import static com.iexec.scheduler.ethereum.Utils.END;
import static com.iexec.scheduler.ethereum.Utils.getStatus;

@Service
public class IexecHubService {

    private static final Logger log = LoggerFactory.getLogger(WorkerPoolService.class);
    private final Web3j web3j;
    private final CredentialsService credentialsService;
    private final WorkerPoolConfig poolConfig;
    private final EthConfig ethConfig;
    private IexecHub iexecHub;
    private IexecHubWatcher iexecHubWatcher;

    @Autowired
    public IexecHubService(Web3j web3j, CredentialsService credentialsService, WorkerPoolConfig poolConfig, EthConfig ethConfig) {
        this.credentialsService = credentialsService;
        this.web3j = web3j;
        this.poolConfig = poolConfig;
        this.ethConfig = ethConfig;
    }

    @PostConstruct
    public void run() throws Exception {
        this.iexecHub = IexecHub.load(
                ethConfig.getIexecHubAddress(), web3j, credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        log.info("Load contract IexecHub [address:{}] ", ethConfig.getIexecHubAddress());
        poolConfig.setAddress(fetchWorkerPoolAddress());
        startWatchers();
    }

    private String fetchWorkerPoolAddress() throws Exception {
        String workerPoolAddress;
        if (poolConfig.getAddress().isEmpty()) {
            TransactionReceipt createWorkerPoolReceipt = iexecHub.createWorkerPool(poolConfig.getName(),
                    poolConfig.getSubscriptionLockStakePolicy(),
                    poolConfig.getSubscriptionMinimumStakePolicy(),
                    poolConfig.getSubscriptionMinimumScorePolicy()).send();
            workerPoolAddress = this.iexecHub.getCreateWorkerPoolEvents(createWorkerPoolReceipt).get(0).workerPool;
            log.info("CreateWorkerPool [address:{}] ", workerPoolAddress);
        } else {
            workerPoolAddress = poolConfig.getAddress();
            log.info("Get WorkerPool address from configuration [address:{}] ", workerPoolAddress);
        }
        return workerPoolAddress;
    }

    private void startWatchers() {
        this.iexecHub.workOrderActivatedEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(this::onWorkOrderActivated);
        this.iexecHub.workerPoolSubscriptionEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(this::onSubscription);
    }

    private void onSubscription(IexecHub.WorkerPoolSubscriptionEventResponse workerPoolSubscriptionEvent) {
        if (workerPoolSubscriptionEvent.workerPool.equals(poolConfig.getAddress())) {
            log.info("Received WorkerPoolSubscriptionEvent [worker:{}]", workerPoolSubscriptionEvent.worker);
            iexecHubWatcher.onSubscription(workerPoolSubscriptionEvent.worker);
        }
    }

    private void onWorkOrderActivated(IexecHub.WorkOrderActivatedEventResponse workOrderActivatedEvent) {
        log.info("Received WorkOrderActivatedEvent [woid:{}]", workOrderActivatedEvent.woid);
        if (workOrderActivatedEvent.workerPool.equals(poolConfig.getAddress())) {
            iexecHubWatcher.onWorkOrderActivated(workOrderActivatedEvent.woid);
        }
    }

    public void register(IexecHubWatcher iexecHubWatcher) {
        this.iexecHubWatcher = iexecHubWatcher;
    }

    public IexecHub getIexecHub() {
        return iexecHub;
    }

}
