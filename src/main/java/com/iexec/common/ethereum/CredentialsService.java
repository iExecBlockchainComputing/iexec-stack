package com.iexec.common.ethereum;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.IOException;

public class CredentialsService {

    private static CredentialsService instance;
    private static WalletConfig walletConfig;
    private Credentials credentials;

    private CredentialsService() {
        walletConfig = IexecConfigurationService.getInstance().getWalletConfig();
        try {
            credentials = WalletUtils.loadCredentials(walletConfig.getPassword(), walletConfig.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }
    }

    public static CredentialsService getInstance() {
        if (instance == null) {
            instance = new CredentialsService();
        }
        return instance;
    }

    public Credentials getCredentials() {
        return credentials;
    }

}
