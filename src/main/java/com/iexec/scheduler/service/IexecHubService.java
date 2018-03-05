package com.iexec.scheduler.service;

import com.iexec.scheduler.contracts.generated.IexecHub;
import com.iexec.scheduler.helper.EthConfig;
import com.iexec.scheduler.helper.WorkerPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import javax.annotation.PostConstruct;

@Service
public class IexecHubService {

    private static final Logger log = LoggerFactory.getLogger(WorkerPoolService.class);

    private final Web3j web3j;
    private final CredentialsService credentialsService;
    private final WorkerPoolConfig poolConfig;
    private final EthConfig ethConfig;
    private IexecHub iexecHub;

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
        createWorkerPool();
    }

    public void createWorkerPool() throws Exception {
        if (poolConfig.getAddress().isEmpty()){
            log.info("SCHEDLR CreateWorkerPool");
            iexecHub.createWorkerPool(poolConfig.getName(),
                    poolConfig.getSubscriptionLockStakePolicy(),
                    poolConfig.getSubscriptionMinimumStakePolicy(),
                    poolConfig.getSubscriptionMinimumScorePolicy()).send();
        }
    }

    public IexecHub getIexecHub() {
        return iexecHub;
    }

}
