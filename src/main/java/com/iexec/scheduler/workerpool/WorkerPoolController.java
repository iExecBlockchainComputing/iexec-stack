package com.iexec.scheduler.workerpool;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workerpool")
public class WorkerPoolController {

    private final WorkerPoolService workerPoolService;
    private final WorkerPoolConfig workerPoolConfig;

    @Autowired
    public WorkerPoolController(WorkerPoolService workerPoolService, WorkerPoolConfig workerPoolConfig) {
        this.workerPoolService = workerPoolService;
        this.workerPoolConfig = workerPoolConfig;
    }

    @RequestMapping
    public WorkerPoolPolicy getPoolConf() throws Exception {
        //TODO - create converter + remove logic from controller
        WorkerPoolPolicy poolPolicy = new WorkerPoolPolicy();
        poolPolicy.setAddress(workerPoolService.getPoolConfig().getAddress());
        poolPolicy.setSubscriptionLockStakePolicy(workerPoolConfig.getSubscriptionLockStakePolicy());
        poolPolicy.setSubscriptionMinimumScorePolicy(workerPoolConfig.getSubscriptionMinimumScorePolicy());
        poolPolicy.setSubscriptionMinimumStakePolicy(workerPoolConfig.getSubscriptionMinimumStakePolicy());
        poolPolicy.setSchedulerRewardRatioPolicy(workerPoolConfig.getSchedulerRewardRatioPolicy());
        poolPolicy.setMode(workerPoolConfig.getMode());
        poolPolicy.setList(workerPoolConfig.getList());
        poolPolicy.setName(workerPoolConfig.getName());
        poolPolicy.setStakeRatioPolicy(workerPoolConfig.getStakeRatioPolicy());
        poolPolicy.setResultRetentionPolicy(workerPoolConfig.getResultRetentionPolicy());
        return poolPolicy;
    }


}