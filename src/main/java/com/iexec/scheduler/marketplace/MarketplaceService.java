package com.iexec.scheduler.marketplace;

import com.iexec.scheduler.contracts.generated.Marketplace;
import com.iexec.scheduler.ethereum.CredentialsService;
import com.iexec.scheduler.ethereum.EthConfig;
import com.iexec.scheduler.iexechub.IexecHubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import javax.annotation.PostConstruct;

@Service
public class MarketplaceService {

    private static final Logger log = LoggerFactory.getLogger(MarketplaceService.class);
    private final Web3j web3j;
    private final CredentialsService credentialsService;
    private final IexecHubService iexecHubService;
    private final EthConfig ethConfig;
    private Marketplace marketplace;

    @Autowired
    public MarketplaceService(Web3j web3j, CredentialsService credentialsService,
                              IexecHubService iexecHubService, EthConfig ethConfig) {
        this.web3j = web3j;
        this.credentialsService = credentialsService;
        this.iexecHubService = iexecHubService;
        this.ethConfig = ethConfig;
    }

    @PostConstruct
    public void run() throws Exception {
        this.marketplace = Marketplace.load(
                iexecHubService.getIexecHub().marketplaceAddress().send(), web3j, credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    public Marketplace getMarketplace() {
        return marketplace;
    }
}
