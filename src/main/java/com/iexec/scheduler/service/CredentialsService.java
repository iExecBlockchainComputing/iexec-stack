package com.iexec.scheduler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class CredentialsService {

    private Credentials credentials;

    @Value("${wallet.folder}")
    private String walletFolder;
    @Value("${scheduler.wallet.filename}")
    private String schedulerWalletFilename;
    @Value("${scheduler.wallet.password}")
    private String schedulerWalletPassword;

    @Autowired
    public CredentialsService() {
    }

    @PostConstruct
    public void run() throws Exception {
        credentials = WalletUtils.loadCredentials(schedulerWalletPassword, walletFolder + "/" + schedulerWalletFilename);
    }

    public Credentials getCredentials() {
        return credentials;
    }
}
