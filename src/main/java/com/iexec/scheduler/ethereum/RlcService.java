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

@Service
public class RlcService {

    private static final Logger log = LoggerFactory.getLogger(RlcService.class);
    private final Web3j web3j;
    private final CredentialsService credentialsService;
    private final EthConfig ethConfig;
    private RLC rlc;

    @Autowired
    public RlcService(Web3j web3j, CredentialsService credentialsService, EthConfig ethConfig) {
        this.credentialsService = credentialsService;
        this.web3j = web3j;
        this.ethConfig = ethConfig;
    }

    @PostConstruct
    public void run() throws Exception {
        log.debug("SCHEDLR loading Rlc contract");
        rlc = RLC.load(
                ethConfig.getRlcAddress(), web3j, credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    public RLC getRlc() {
        return this.rlc;
    }

}
