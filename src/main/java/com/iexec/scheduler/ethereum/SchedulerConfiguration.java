package com.iexec.scheduler.ethereum;

import com.iexec.common.ethereum.CommonConfiguration;
import com.iexec.common.ethereum.CommonConfigurationService;
import com.iexec.common.workerpool.WorkerPoolConfig;

public class SchedulerConfiguration {

    private WorkerPoolConfig workerPoolConfig;

    public CommonConfiguration getCommonConfiguration() {
        return CommonConfigurationService.getInstance().getConfiguration();
    }

    public void setCommonConfiguration(CommonConfiguration commonConfiguration) {
        CommonConfigurationService.getInstance().setConfiguration(commonConfiguration);
    }

    public WorkerPoolConfig getWorkerPoolConfig() {
        return workerPoolConfig;
    }

    public void setWorkerPoolConfig(WorkerPoolConfig workerPoolConfig) {
        this.workerPoolConfig = workerPoolConfig;
    }
}