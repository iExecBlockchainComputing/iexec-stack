package com.iexec.scheduler.sandbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;

@RestController
public class AuthController {

    private static final String CHALLENGE = "iexec";

    @Autowired
    public AuthController() {
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