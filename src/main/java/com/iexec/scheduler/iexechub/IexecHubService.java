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
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import javax.annotation.PostConstruct;

import static com.iexec.scheduler.ethereum.Utils.getStatus;

@Service
public class IexecHubService {

    private static final Logger log = LoggerFactory.getLogger(WorkerPoolService.class);
    private final Web3j web3j;
    private final CredentialsService credentialsService;
    private final WorkerPoolConfig poolConfig;
    private final EthConfig ethConfig;
    private IexecHub iexecHub;
    private String workerPoolAddress;

    @Autowired
    public IexecHubService(Web3j web3j, CredentialsService credentialsService, WorkerPoolConfig poolConfig, EthConfig ethConfig) {
        this.credentialsService = credentialsService;
        this.web3j = web3j;
        this.poolConfig = poolConfig;
        this.ethConfig = ethConfig;
    }

    @PostConstruct
    public void run() throws Exception {
        log.info("SCHEDLR loading iexecHub");
        this.iexecHub = IexecHub.load(
                ethConfig.getIexecHubAddress(), web3j, credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        this.workerPoolAddress = fetchWorkerPoolAddress();
    }

    private String fetchWorkerPoolAddress() throws Exception {
        if (poolConfig.getAddress().isEmpty()) {
            TransactionReceipt createWorkerPoolReceipt = iexecHub.createWorkerPool(poolConfig.getName(),
                    poolConfig.getSubscriptionLockStakePolicy(),
                    poolConfig.getSubscriptionMinimumStakePolicy(),
                    poolConfig.getSubscriptionMinimumScorePolicy()).send();
            log.info("SCHEDLR createWorkerPool " + getStatus(createWorkerPoolReceipt));
            return this.iexecHub.getCreateWorkerPoolEvents(createWorkerPoolReceipt).get(0).workerPool;
        } else {
            log.info("SCHEDLR fetch WorkerPool address from conf");
            return poolConfig.getAddress();
        }
    }

    public IexecHub getIexecHub() {
        return iexecHub;
    }

    public String getWorkerPoolAddress() {
        return workerPoolAddress;
    }
}
