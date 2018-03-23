package com.iexec.scheduler.ethereum;

import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.IOException;

@Service
public class CredentialsService {

    private static CredentialsService instance;
    private Credentials credentials;
    private static WalletConfig walletConfig;

    public static CredentialsService getInstance() {
        if (instance==null){
            instance = new CredentialsService();
        }
        return instance;
    }

    private CredentialsService() {
        walletConfig = ConfigurationService.getInstance().getConfiguration().getWalletConfig();
        try {
            credentials = WalletUtils.loadCredentials(walletConfig.getPassword(), walletConfig.getFolder() + "/" + walletConfig.getFilename());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public static void main(String[] args) {
        ConfigurationService.getInstance().setConfigPath("./iexec-scheduler/src/main/resources/application.yml");
        System.out.println(CredentialsService.getInstance().getCredentials().getAddress());
    }

}
