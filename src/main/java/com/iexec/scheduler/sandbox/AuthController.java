package com.iexec.scheduler.sandbox;

import com.iexec.scheduler.mock.MockWatcherService;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;

@RestController
public class AuthController {

    private final MockWatcherService mockService;
    private final WorkerPoolService workerPoolService;
    private static final String CHALLENGE = "iexec";

    @Autowired
    public AuthController(MockWatcherService mockService, WorkerPoolService workerPoolService) {
        this.mockService = mockService;
        this.workerPoolService = workerPoolService;
    }

    @RequestMapping("/challenge")
    public String getChallenge() throws Exception {
        return CHALLENGE;
    }


    @PostMapping("/signchallenge")
    public String postChallengeSigned(@RequestBody Signature signature) throws Exception {
        String pubkey = Sign.signedMessageToKey(CHALLENGE.getBytes(), new Sign.SignatureData(signature.getV(), signature.getR(), signature.getS())).toString(16);
        return Keys.getAddress(pubkey);
    }
}