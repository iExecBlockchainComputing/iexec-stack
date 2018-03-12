package com.iexec.worker.iexechub;

import com.iexec.scheduler.contracts.generated.IexecHub;
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
public class IexecHubService {

    private static final Logger log = LoggerFactory.getLogger(IexecHubService.class);
    private final Web3j web3j;
    private final CredentialsService credentialsService;
    private final EthConfig ethConfig;
    private final SchedulerApiService schedulerApiService;
    private IexecHub iexecHub;

    @Autowired
    public IexecHubService(Web3j web3j, CredentialsService credentialsService, EthConfig ethConfig, SchedulerApiService schedulerApiService) {
        this.credentialsService = credentialsService;
        this.web3j = web3j;
        this.ethConfig = ethConfig;
        this.schedulerApiService = schedulerApiService;
    }

    @PostConstruct
    public void run() throws Exception {
        log.info("SCHEDLR loading iexecHub");
        this.iexecHub = IexecHub.load(
                schedulerApiService.getIexecHub(), web3j, credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    public IexecHub getIexecHub() {
        return iexecHub;
    }

}
