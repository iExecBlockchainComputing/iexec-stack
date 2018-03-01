package com.iexec.scheduler.controller;

import com.iexec.scheduler.service.MockWatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
public class MockController {

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
                                   @RequestParam(defaultValue = "0") String dataset,
                                   @RequestParam(defaultValue = "noTaskParam") String workOrderParam,
                                   @RequestParam(defaultValue = "0") BigInteger workReward,
                                   @RequestParam(defaultValue = "1") BigInteger askedTrust,
                                   @RequestParam(defaultValue = "false") Boolean dappCallback,
                                   @RequestParam String beneficiary) throws Exception {
        return mockService.createWorkOrder(mockService.getWorkerPoolAddress(), app, dataset, workOrderParam, workReward, askedTrust, dappCallback, beneficiary);
    }


}