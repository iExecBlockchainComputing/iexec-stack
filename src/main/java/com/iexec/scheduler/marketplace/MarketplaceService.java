package com.iexec.scheduler.marketplace;

import com.iexec.common.contracts.generated.Marketplace;
import com.iexec.common.ethereum.CredentialsService;
import com.iexec.common.ethereum.TransactionStatus;
import com.iexec.common.ethereum.Utils;
import com.iexec.common.ethereum.Web3jService;
import com.iexec.common.model.MarketOrderModel;
import com.iexec.scheduler.iexechub.IexecHubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.tuples.generated.Tuple8;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.math.BigInteger;

import static com.iexec.common.ethereum.Utils.tuple2MarketOrderModel;


public class MarketplaceService {

    private static final Logger log = LoggerFactory.getLogger(MarketplaceService.class);
    private static MarketplaceService instance;
    private final IexecHubService iexecHubService = IexecHubService.getInstance();
    private final Web3jService web3jService = Web3jService.getInstance();
    private final CredentialsService credentialsService = CredentialsService.getInstance();
    private Marketplace marketplace;

    private MarketplaceService() {
        ExceptionInInitializerError exceptionInInitializerError = new ExceptionInInitializerError("Failed to load Marketplace contract");
        if (iexecHubService!=null){
            try {
                String marketplaceAddress = iexecHubService.getIexecHub().marketplaceAddress().send();
                this.marketplace = Marketplace.load(
                        marketplaceAddress, web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
            } catch (Exception e) {
                throw  exceptionInInitializerError;
            }
        } else {
            throw  exceptionInInitializerError;
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

    public MarketOrderModel getMarketOrderModel(BigInteger marketOrderIdx){
        MarketOrderModel marketOrderModel = null;
        TransactionStatus transactionStatus = TransactionStatus.SUCCESS;
        try {
            marketOrderModel = tuple2MarketOrderModel(marketplace.getMarketOrder(marketOrderIdx).send());
        } catch (Exception e) {
            transactionStatus = TransactionStatus.FAILURE;
        }
        log.info("GetMarketOrder [marketOrderIdx:{}, transactionStatus:{}] ",
                marketOrderIdx, transactionStatus);
        return marketOrderModel;
    }

}
