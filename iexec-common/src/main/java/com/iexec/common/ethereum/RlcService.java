package com.iexec.common.ethereum;


import com.iexec.common.contracts.generated.RLC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.ens.EnsResolutionException;


public class RlcService {

    private static final Logger log = LoggerFactory.getLogger(RlcService.class);
    private static final CommonConfiguration configuration = IexecConfigurationService.getInstance().getCommonConfiguration();
    private static final Web3jService web3jService = Web3jService.getInstance();
    private static final CredentialsService credentialsService = CredentialsService.getInstance();
    private static RlcService instance;
    private RLC rlc;

    private RlcService() {
        String rlcAddress = configuration.getContractConfig().getRlcAddress();
        ExceptionInInitializerError exceptionInInitializerError = new ExceptionInInitializerError("Failed to load RLC contract from address " + rlcAddress);
        if (rlcAddress != null && !rlcAddress.isEmpty()) {
            try {
                rlc = RLC.load(
                        rlcAddress, web3jService.getWeb3j(), credentialsService.getCredentials(), configuration.getNodeConfig().getGasPrice(), configuration.getNodeConfig().getGasLimit());
                //if (!rlc.isValid()){ throw exceptionInInitializerError;}
            } catch (EnsResolutionException e) {
                throw exceptionInInitializerError;
            }
        } else {
            throw exceptionInInitializerError;
        }
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
