package com.iexec.scheduler.iexechub;

import com.iexec.scheduler.contracts.generated.IexecHub;
import com.iexec.scheduler.ethereum.CredentialsService;
import com.iexec.scheduler.ethereum.EthConfig;
import com.iexec.scheduler.ethereum.Utils;
import com.iexec.scheduler.workerpool.WorkerPoolConfig;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import javax.annotation.PostConstruct;

import static com.iexec.scheduler.ethereum.Utils.cutLeadingZeros;

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
            log.info("SCHEDLR CreateWorkerPool");
            TransactionReceipt receipt = iexecHub.createWorkerPool(poolConfig.getName(),
                    poolConfig.getSubscriptionLockStakePolicy(),
                    poolConfig.getSubscriptionMinimumStakePolicy(),
                    poolConfig.getSubscriptionMinimumScorePolicy()).send();

            for (Log receiptLog : receipt.getLogs()) {
                String createWorkerPoolFilter = Hash.sha3String("CreateWorkerPool(address,address,string)");
                if (receiptLog.getTopics().get(0).equals(createWorkerPoolFilter)) {//0: sha3('CreateWorkerPool(address,address,string)')
                    return cutLeadingZeros(receiptLog.getTopics().get(2)); //0: , 1: tx.origin, 2: workerPoolAddress, data: workerPoolName
                }
            }
        } else {
            log.info("SCHEDLR fetch WorkerPool address from conf");
            return poolConfig.getAddress();
        }
        throw new IllegalStateException("Unable to get WorkerPool address");
    }

    public IexecHub getIexecHub() {
        return iexecHub;
    }

    public String getWorkerPoolAddress() {
        return workerPoolAddress;
    }
}
