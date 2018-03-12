package com.iexec.worker.ethereum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;

import javax.annotation.PostConstruct;

@Service
public class CredentialsService {

    private static final long UNLOCK_DURATION = 300;
    private final Web3j web3j;
    private Credentials credentials;

    @Value("${wallet.folder}")
    private String walletFolder;
    @Value("${worker.address}")
    private String workerAdress;
    @Value("${worker.wallet.filename}")
    private String workerWalletFilename;
    @Value("${worker.wallet.password}")
    private String workerWalletPassword;

    @Autowired
    public CredentialsService(Web3j web3j) {
        this.web3j = web3j;
    }

    @PostConstruct
    public void run() throws Exception {
        credentials = WalletUtils.loadCredentials(workerWalletPassword, walletFolder + "/" + workerWalletFilename);
    }

    public Credentials getCredentials() {
        return credentials;
    }
}
