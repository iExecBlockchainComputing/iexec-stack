package com.iexec.worker.ethereum;


import com.iexec.worker.contracts.generated.RLC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;


public class RlcService {

    private static final Logger log = LoggerFactory.getLogger(RlcService.class);
    private static final Configuration configuration = IexecConfigurationService.getInstance().getConfiguration();
    private static final Web3jService web3jService = Web3jService.getInstance();
    private static final CredentialsService credentialsService = CredentialsService.getInstance();
    private static RlcService instance;
    private RLC rlc;

    private RlcService() {
        rlc = RLC.load(
                configuration.getContractConfig().getRlcAddress(), web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    public static RlcService getInstance() {
        if (instance == null) {
            instance = new RlcService();
        }
        return instance;
    }

    public RLC getRlc() {
        return this.rlc;
    }

}
