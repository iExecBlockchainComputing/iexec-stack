package com.iexec.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyController {

    private Web3jService web3jService;

    @Autowired
    public DummyController(Web3jService web3jService) {
        this.web3jService = web3jService;
    }

    @RequestMapping("/")
    public String dummy() throws Exception {
        web3jService.printAdresses();
        return "Hello World!";
    }
}