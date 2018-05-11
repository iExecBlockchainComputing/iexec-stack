package com.iexec.common.model;


import com.iexec.common.contracts.generated.*;
import com.iexec.common.ethereum.CredentialsService;
import com.iexec.common.ethereum.IexecConfigurationService;
import com.iexec.common.ethereum.Web3jService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.tuples.generated.Tuple8;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.math.BigInteger;


public class ModelService {

    private static final Logger log = LoggerFactory.getLogger(ModelService.class);
    private static final Web3jService web3jService = Web3jService.getInstance();
    private static final CredentialsService credentialsService = CredentialsService.getInstance();
    private static ModelService instance;

    public static ModelService getInstance() {
        if (instance == null) {
            instance = new ModelService();
        }
        return instance;
    }

    public AppModel getAppModel(String appId) {
        try {
            App app = App.load(
                    appId, web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
            return new AppModel(app.m_owner().send(), app.m_appName().send(), app.m_appPrice().send(), app.m_appParams().send());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public DatasetModel getDatasetModel(String datasetId) {
        try {
            Dataset dataset = Dataset.load(
                    datasetId, web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
            return new DatasetModel(dataset.m_owner().send(), dataset.m_datasetName().send(), dataset.m_datasetPrice().send(), dataset.m_datasetParams().send());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public WorkOrderModel getWorkOrderModel(String workOrderId) {
        try {
            WorkOrder workOrder = WorkOrder.load(
                    workOrderId, web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
            return new WorkOrderModel(workOrder.m_marketorderIdx().send(), workOrder.m_requester().send(), workOrder.m_app().send(), workOrder.m_dataset().send(), workOrder.m_workerpool().send(), workOrder.m_emitcost().send(), workOrder.m_params().send(), workOrder.m_callback().send(), workOrder.m_beneficiary().send());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public MarketOrderModel getMarketOrderIdx(BigInteger marketOrderIdx) {
        try {
            String iexecHubAddress = IexecConfigurationService.getInstance().getCommonConfiguration().getContractConfig().getIexecHubAddress();
            IexecHub iexecHub = IexecHub.load(
                    iexecHubAddress, web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
            Marketplace marketplace = Marketplace.load(
                    iexecHub.marketplaceAddress().send(), web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
            Tuple8<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, String> marketOrder = marketplace.getMarketOrder(marketOrderIdx).send();
            return new MarketOrderModel(marketOrder.getValue1(), marketOrder.getValue2(), marketOrder.getValue3(), marketOrder.getValue4(), marketOrder.getValue5(), marketOrder.getValue6(), marketOrder.getValue7(), marketOrder.getValue8());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
