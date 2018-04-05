package com.iexec.scheduler.marketplace;

import com.iexec.common.contracts.generated.Marketplace;
import com.iexec.common.ethereum.CredentialsService;
import com.iexec.common.ethereum.Web3jService;
import com.iexec.scheduler.iexechub.IexecHubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;


public class MarketplaceService {

    private static final Logger log = LoggerFactory.getLogger(MarketplaceService.class);
    private static MarketplaceService instance;
    private final IexecHubService iexecHubService = IexecHubService.getInstance();
    private final Web3jService web3jService = Web3jService.getInstance();
    private final CredentialsService credentialsService = CredentialsService.getInstance();
    private Marketplace marketplace;

    private MarketplaceService() {
        try {
            this.marketplace = Marketplace.load(
                    iexecHubService.getIexecHub().marketplaceAddress().send(), web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MarketplaceService getInstance() {
        if (instance == null) {
            instance = new MarketplaceService();
        }
        return instance;
    }

    public Marketplace getMarketplace() {
        return marketplace;
    }

}
