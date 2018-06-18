package com.iexec.common.ethereum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.IOException;

public class CredentialsService {

    private static final Logger log = LoggerFactory.getLogger(CredentialsService.class);
    private static CredentialsService instance;
    private static WalletConfig walletConfig;
    private Credentials credentials;

    private CredentialsService() {
        walletConfig = IexecConfigurationService.getInstance().getWalletConfig();
        try {
            credentials = WalletUtils.loadCredentials(walletConfig.getPassword(), walletConfig.getPath());
            log.info("Load wallet credentials [address:{}] ", credentials.getAddress());
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
