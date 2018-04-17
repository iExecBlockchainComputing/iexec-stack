package com.iexec.common.ethereum;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;


public class Web3jService {

    private static Web3jService instance;
    private Web3j web3j;

    private Web3jService() {
        String ethNodeAddress = IexecConfigurationService.getInstance().getCommonConfiguration().getNodeConfig().getClientAddress();
        ExceptionInInitializerError exceptionInInitializerError = new ExceptionInInitializerError("Failed to connect to ethereum node " + ethNodeAddress);
        web3j = Web3j.build(new HttpService(ethNodeAddress));  // defaults to http://localhost:8545/
        try {
            if (web3j.web3ClientVersion().send().getWeb3ClientVersion() == null) {
                throw exceptionInInitializerError;
            }
        } catch (IOException e) {
            throw exceptionInInitializerError;
        }
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
