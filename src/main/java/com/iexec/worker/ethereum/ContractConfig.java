package com.iexec.worker.ethereum;

import java.math.BigInteger;

public class ContractConfig {

    private String iexecHubAddress;
    private String rlcAddress;
    private String workerPoolAddress;
    private BigInteger subscriptionMinimumStakePolicy;
    private BigInteger stakeRatioPolicy;

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

    public String getWorkerPoolAddress() {
        return workerPoolAddress;
    }

    public void setWorkerPoolAddress(String workerPoolAddress) {
        this.workerPoolAddress = workerPoolAddress;
    }

    public BigInteger getSubscriptionMinimumStakePolicy() {
        return subscriptionMinimumStakePolicy;
    }

    public void setSubscriptionMinimumStakePolicy(BigInteger subscriptionMinimumStakePolicy) {
        this.subscriptionMinimumStakePolicy = subscriptionMinimumStakePolicy;
    }

    public BigInteger getStakeRatioPolicy() {
        return stakeRatioPolicy;
    }

    public void setStakeRatioPolicy(BigInteger stakeRatioPolicy) {
        this.stakeRatioPolicy = stakeRatioPolicy;
    }
}
