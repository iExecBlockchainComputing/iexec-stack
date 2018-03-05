package com.iexec.scheduler.mock;

import com.iexec.scheduler.workerpool.WorkerPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

@RestController
public class MockController {

    private MockWatcherService mockService;
    private WorkerPoolService workerPoolService;


    @Autowired
    public MockController(MockWatcherService mockService, WorkerPoolService workerPoolService) {
        this.mockService = mockService;
        this.workerPoolService = workerPoolService;
    }

    @RequestMapping("/isalive")
    public boolean isAlive() throws Exception {
        return true;
    }


    @PostMapping("/workerpool")
    public String getWorkerpool() throws Exception {
        return workerPoolService.getWorkerPoolAddress();
    }

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
}