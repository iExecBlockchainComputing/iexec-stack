package com.iexec.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;

import java.math.BigInteger;

@RestController
public class MockController {

    private static final Logger log = LoggerFactory.getLogger(MockController.class);

    @Value("${ethereum.address.iexecHub}")
    private String iexecHubAddress;

    private MockWatcherService mockService;

    @Autowired
    public MockController(MockWatcherService mockService) {
        this.mockService = mockService;
    }

    @RequestMapping("/isalive")
    public boolean isAlive() throws Exception {
        return true;
    }

    @RequestMapping("/workerpool")
    public String getWorkerpool() throws Exception {
        return mockService.getWorkerPoolAddress();
    }

    @RequestMapping("/createworkorder")
    public boolean createWorkOrder(@RequestParam String app,
                                   @RequestParam String clouduser) throws Exception {
        boolean gasOk = false;

        if (mockService.isWorkerSubscribed()){
            TransactionReceipt tr = mockService.getIexecHubForScheduler().createWorkOrder(mockService.getWorkerPoolAddress(), app, "0", "noTaskParam", BigInteger.ZERO, BigInteger.ONE, false, clouduser).send();
            gasOk = !tr.getGasUsed().equals(Contract.GAS_LIMIT);
        }

        return gasOk;
    }
}