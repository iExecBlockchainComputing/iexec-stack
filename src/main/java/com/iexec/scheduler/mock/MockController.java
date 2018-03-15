package com.iexec.scheduler.mock;

import com.iexec.scheduler.contracts.generated.IexecHub;
import com.iexec.scheduler.ethereum.EthConfig;
import com.iexec.scheduler.iexechub.IexecHubService;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

@RestController
public class MockController {

    private final IexecHubService iexecHubService;
    private final EthConfig ethConfig;

    @Autowired
    public MockController(IexecHubService iexecHubService, EthConfig ethConfig) {
        this.iexecHubService = iexecHubService;
        this.ethConfig = ethConfig;
    }

    @RequestMapping("/isalive")
    public boolean isAlive() throws Exception {
        return true;
    }

    @RequestMapping("/iexechub")
    public String getIexecHub() throws Exception {
        return iexecHubService.getIexecHub().getContractAddress();
    }

    @RequestMapping("/rlc")
    public String getRlc() throws Exception {
        return ethConfig.getRlcAddress();
    }

    /*
    //TODO - Change to @PostMapping
    @RequestMapping("/createworkorder")
    public TransactionReceipt createWorkOrder(@RequestParam String app,
                                              @RequestParam(defaultValue = "0") String dataset,
                                              @RequestParam(defaultValue = "noTaskParam") String workOrderParam,
                                              @RequestParam(defaultValue = "0") BigInteger workReward,
                                              @RequestParam(defaultValue = "1") BigInteger askedTrust,
                                              @RequestParam(defaultValue = "false") Boolean dappCallback,
                                              @RequestParam String beneficiary) throws Exception {
        return mockService.createWorkOrder(workerPoolService.getWorkerPoolAddress(), app, dataset, workOrderParam, workReward, askedTrust, dappCallback, beneficiary);
    }
    */
}