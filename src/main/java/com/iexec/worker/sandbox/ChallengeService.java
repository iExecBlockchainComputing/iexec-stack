package com.iexec.worker.sandbox;

import com.iexec.worker.ethereum.CredentialsService;
import com.iexec.worker.scheduler.SchedulerApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.crypto.Sign;

import javax.annotation.PostConstruct;

import static org.web3j.utils.Numeric.cleanHexPrefix;

//@Service
public class ChallengeService {

    private static final Logger log = LoggerFactory.getLogger(ChallengeService.class);
    private final CredentialsService credentialsService;
    private final SchedulerApiService schedulerApiService;


    @Autowired
    public ChallengeService(CredentialsService credentialsService, SchedulerApiService schedulerApiService) {
        this.credentialsService = credentialsService;
        this.schedulerApiService = schedulerApiService;
    }

    @PostConstruct
    public void run() throws Exception {
        String message = schedulerApiService.getChallenge();
        Sign.SignatureData signatureData = Sign.signMessage(message.getBytes(), credentialsService.getCredentials().getEcKeyPair());
        Signature signature = new Signature(signatureData.getV(), signatureData.getR(), signatureData.getS());
        log.info(schedulerApiService.getChallenge());
        String address = schedulerApiService.postSignedChallenge(signature);
        log.info("public address from signed challenge: " + address);
        log.info("is worker address? " + address.equals(cleanHexPrefix(credentialsService.getCredentials().getAddress())));

    }
}
