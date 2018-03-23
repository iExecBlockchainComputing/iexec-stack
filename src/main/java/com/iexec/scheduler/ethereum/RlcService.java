package com.iexec.scheduler.ethereum;


import com.iexec.scheduler.contracts.generated.RLC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import javax.annotation.PostConstruct;


public class RlcService {

    /*

    private static final Logger log = LoggerFactory.getLogger(RlcService.class);
    private final Web3j web3j;
    private final CredentialsService credentialsService;
    private final Web3jConfig web3jConfig;
    private RLC rlc;

    @Autowired
    public RlcService(Web3j web3j, CredentialsService credentialsService, Web3jConfig web3jConfig) {
        this.credentialsService = credentialsService;
        this.web3j = web3j;
        this.web3jConfig = web3jConfig;
    }

    @PostConstruct
    public void run() throws Exception {
        rlc = RLC.load(
                web3jConfig.getRlcAddress(), web3j, credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    public RLC getRlc() {
        return this.rlc;
    }*/

}
