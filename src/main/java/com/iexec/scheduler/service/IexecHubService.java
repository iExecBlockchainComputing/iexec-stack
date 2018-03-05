package com.iexec.scheduler.service;

import com.iexec.scheduler.contracts.generated.IexecHub;
import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.helper.WorkerPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import rx.Subscriber;
import rx.Subscription;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Observable;

@Service
public class IexecHubService {

    private static final Logger log = LoggerFactory.getLogger(WorkerPoolService.class);

    private final Web3j web3j;
    private final CredentialsService credentialsService;
    private IexecHub iexecHub;
    private String workerPoolAddress;

    @Value("${ethereum.address.iexecHub}")
    private String iexecHubAddress;

    @Value("${ethereum.startBlock}")
    private BigInteger startBlock;

    @Autowired
    private WorkerPoolConfig poolConfig;

    @Autowired
    public IexecHubService(Web3j web3j, CredentialsService credentialsService) {
        this.credentialsService = credentialsService;
        this.web3j = web3j;
    }

    @PostConstruct
    public void run() throws Exception {
        log.info("SCHEDLR loading iexecHub");
        this.iexecHub = IexecHub.load(
                iexecHubAddress, web3j, credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        createWorkerPool();
    }

    public IexecHub getIexecHub() {
        return iexecHub;
    }

    //TODO
    //check is first time or something like that
    public void createWorkerPool() throws Exception {
        log.info("SCHEDLR CreateWorkerPool");
        iexecHub.createWorkerPool(poolConfig.getName(),
                poolConfig.getSubscriptionLockStakePolicy(),
                poolConfig.getSubscriptionMinimumStakePolicy(),
                poolConfig.getSubscriptionMinimumScorePolicy()).send();
    }

    private DefaultBlockParameter getStartBlock() {
        return DefaultBlockParameter.valueOf(startBlock);
    }

    
}
