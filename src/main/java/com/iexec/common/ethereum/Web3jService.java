package com.iexec.common.ethereum;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;


public class Web3jService {

    private static Web3jService instance;


    private Web3j web3j;

    private Web3jService() {
        web3j = Web3j.build(new HttpService(CommonConfigurationService.getInstance().getConfiguration().getWeb3jConfig().getClientAddress()));  // defaults to http://localhost:8545/
    }


    public static Web3jService getInstance() {
        if (instance == null) {
            instance = new Web3jService();
        }
        return instance;
    }

    public Web3j getWeb3j() {
        return web3j;
    }
}
