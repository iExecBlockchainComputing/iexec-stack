package com.iexec.worker.iexechub;

import com.iexec.worker.contracts.generated.IexecHub;
import com.iexec.worker.ethereum.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

public class IexecHubService {

    private static final Logger log = LoggerFactory.getLogger(IexecHubService.class);
    private static IexecHubService instance;
    private final Web3jService web3jService = Web3jService.getInstance();
    private final CredentialsService credentialsService = CredentialsService.getInstance();
    private final Configuration configuration = IexecConfigurationService.getInstance().getConfiguration();
    private final ContractConfig contractConfig = configuration.getContractConfig();

    private IexecHub iexecHub;

    private IexecHubService() {
        run();
    }

    public static IexecHubService getInstance() {
        if (instance == null) {
            instance = new IexecHubService();
        }
        return instance;
    }

    public void run() {
        this.iexecHub = IexecHub.load(
                contractConfig.getIexecHubAddress(), web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    public IexecHub getIexecHub() {
        return iexecHub;
    }

}
