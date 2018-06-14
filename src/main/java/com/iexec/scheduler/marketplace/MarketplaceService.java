package com.iexec.scheduler.marketplace;

import com.iexec.common.contracts.generated.Marketplace;
import com.iexec.common.ethereum.*;
import com.iexec.common.model.MarketOrderModel;
import com.iexec.scheduler.iexechub.IexecHubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

import static com.iexec.common.ethereum.Utils.getTransactionStatusFromEvents;
import static com.iexec.common.ethereum.Utils.tuple2MarketOrderModel;


public class MarketplaceService {

    private static final Logger log = LoggerFactory.getLogger(MarketplaceService.class);
    private static final CommonConfiguration configuration = IexecConfigurationService.getInstance().getCommonConfiguration();
    private static MarketplaceService instance;
    private final IexecHubService iexecHubService = IexecHubService.getInstance();
    private final Web3jService web3jService = Web3jService.getInstance();
    private final CredentialsService credentialsService = CredentialsService.getInstance();
    private Marketplace marketplace;

    private MarketplaceService() {
        ExceptionInInitializerError exceptionInInitializerError = new ExceptionInInitializerError("Failed to load Marketplace contract");
        if (iexecHubService != null) {
            try {
                String marketplaceAddress = iexecHubService.getIexecHub().marketplace().send();
                this.marketplace = Marketplace.load(
                        marketplaceAddress, web3jService.getWeb3j(), credentialsService.getCredentials(), configuration.getNodeConfig().getGasPrice(), configuration.getNodeConfig().getGasLimit());
            } catch (Exception e) {
                throw exceptionInInitializerError;
            }
        } else {
            throw exceptionInInitializerError;
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

    public MarketOrderModel getMarketOrderModel(BigInteger marketOrderIdx) {
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

    public BigInteger getMarketOrderCount() {
        BigInteger marketOrderCount = null;
        TransactionStatus transactionStatus = TransactionStatus.SUCCESS;
        try {
            marketOrderCount = marketplace.m_orderCount().send();
        } catch (Exception e) {
            transactionStatus = TransactionStatus.FAILURE;
        }
        log.info("GetMarketOrderCount [marketOrderCount:{}, transactionStatus:{}] ",
                marketOrderCount, transactionStatus);
        return marketOrderCount;
    }

    public TransactionStatus closeMarketOrder(BigInteger marketOrderIdx) {
        try {
            TransactionReceipt closeMarketOrderReceipt = marketplace.closeMarketOrder(marketOrderIdx).send();
            List<Marketplace.MarketOrderClosedEventResponse> marketOrderClosedEvents = marketplace.getMarketOrderClosedEvents(closeMarketOrderReceipt);
            log.info("CloseMarketOrder [marketOrderIdx:{}, transactionHash:{}, transactionStatus:{}] ",
                    marketOrderIdx, closeMarketOrderReceipt.getTransactionHash(), getTransactionStatusFromEvents(marketOrderClosedEvents));
            return getTransactionStatusFromEvents(marketOrderClosedEvents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }

}
