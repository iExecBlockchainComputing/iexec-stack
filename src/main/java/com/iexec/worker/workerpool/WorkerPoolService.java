package com.iexec.worker.workerpool;

import com.iexec.worker.contracts.generated.WorkerPool;
import com.iexec.worker.ethereum.CredentialsService;
import com.iexec.worker.ethereum.EthConfig;
import com.iexec.worker.scheduler.SchedulerApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import javax.annotation.PostConstruct;

@Service
public class WorkerPoolService {

    private static final Logger log = LoggerFactory.getLogger(WorkerPoolService.class);
    private final Web3j web3j;
    private final CredentialsService credentialsService;
    private final EthConfig ethConfig;
    private final SchedulerApiService schedulerApiService;
    private WorkerPool workerPool;

    @Autowired
    public WorkerPoolService(Web3j web3j, CredentialsService credentialsService, EthConfig ethConfig, SchedulerApiService schedulerApiService) {
        this.credentialsService = credentialsService;
        this.web3j = web3j;
        this.ethConfig = ethConfig;
        this.schedulerApiService = schedulerApiService;
    }

    @PostConstruct
    public void run() throws Exception {
        String workerPoolAddress = schedulerApiService.getWorkerPoolPolicy().getAddress();
        log.info("WORKER1 loading WorkerPool contract on " + workerPoolAddress);

        if (workerPoolAddress != null) {
            workerPool = WorkerPool.load(
                    workerPoolAddress, web3j, credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

        }
    }

    public WorkerPool getWorkerPool() {
        return workerPool;
    }

}
