package com.iexec.common.model;


import com.iexec.common.contracts.generated.App;
import com.iexec.common.contracts.generated.Dataset;
import com.iexec.common.contracts.generated.WorkOrder;
import com.iexec.common.ethereum.CommonConfiguration;
import com.iexec.common.ethereum.CredentialsService;
import com.iexec.common.ethereum.IexecConfigurationService;
import com.iexec.common.ethereum.Web3jService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;


public class ModelService {

    private static final Logger log = LoggerFactory.getLogger(ModelService.class);
    private static final CommonConfiguration configuration = IexecConfigurationService.getInstance().getCommonConfiguration();
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
                    appId, web3jService.getWeb3j(), credentialsService.getCredentials(), configuration.getNodeConfig().getGasPrice(), configuration.getNodeConfig().getGasLimit());
            return new AppModel(appId, app.m_owner().send(), app.m_appName().send(), app.m_appPrice().send(), app.m_appParams().send());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public DatasetModel getDatasetModel(String datasetId) {
        try {
            Dataset dataset = Dataset.load(
                    datasetId, web3jService.getWeb3j(), credentialsService.getCredentials(), configuration.getNodeConfig().getGasPrice(), configuration.getNodeConfig().getGasLimit());
            return new DatasetModel(datasetId, dataset.m_owner().send(), dataset.m_datasetName().send(), dataset.m_datasetPrice().send(), dataset.m_datasetParams().send());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public WorkOrderModel getWorkOrderModel(String workOrderId) {
        try {
            WorkOrder workOrder = WorkOrder.load(
                    workOrderId, web3jService.getWeb3j(), credentialsService.getCredentials(), configuration.getNodeConfig().getGasPrice(), configuration.getNodeConfig().getGasLimit());
            return new WorkOrderModel(workOrderId, workOrder.m_status().send(), workOrder.m_marketorderIdx().send(), workOrder.m_requester().send(), workOrder.m_app().send(), workOrder.m_dataset().send(), workOrder.m_workerpool().send(), workOrder.m_emitcost().send(), workOrder.m_params().send(), workOrder.m_callback().send(), workOrder.m_beneficiary().send());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
