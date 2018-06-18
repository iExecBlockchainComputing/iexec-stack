package com.iexec.common.ethereum;

import com.iexec.common.workerpool.WorkerPoolConfig;

public class ContractConfig {

    private String iexecHubAddress;
    private String rlcAddress;
    private WorkerPoolConfig workerPoolConfig;

    public String getIexecHubAddress() {
        return iexecHubAddress;
    }

    public void setIexecHubAddress(String iexecHubAddress) {
        this.iexecHubAddress = iexecHubAddress;
    }

    public String getRlcAddress() {
        return rlcAddress;
    }

    public void setRlcAddress(String rlcAddress) {
        this.rlcAddress = rlcAddress;
    }

    public WorkerPoolConfig getWorkerPoolConfig() {
        return workerPoolConfig;
    }

    public void setWorkerPoolConfig(WorkerPoolConfig workerPoolConfig) {
        this.workerPoolConfig = workerPoolConfig;
    }
}
