package com.iexec.common.workerpool;

import java.math.BigInteger;


public class WorkerPoolConfig {

    private String address;
    private String name;
    private BigInteger subscriptionLockStakePolicy;
    private BigInteger subscriptionMinimumStakePolicy;
    private BigInteger subscriptionMinimumScorePolicy;
    private BigInteger stakeRatioPolicy;
    private BigInteger schedulerRewardRatioPolicy;

    public WorkerPoolConfig() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getSubscriptionLockStakePolicy() {
        return subscriptionLockStakePolicy;
    }

    public void setSubscriptionLockStakePolicy(BigInteger subscriptionLockStakePolicy) {
        this.subscriptionLockStakePolicy = subscriptionLockStakePolicy;
    }

    public BigInteger getSubscriptionMinimumStakePolicy() {
        return subscriptionMinimumStakePolicy;
    }

    public void setSubscriptionMinimumStakePolicy(BigInteger subscriptionMinimumStakePolicy) {
        this.subscriptionMinimumStakePolicy = subscriptionMinimumStakePolicy;
    }

    public BigInteger getSubscriptionMinimumScorePolicy() {
        return subscriptionMinimumScorePolicy;
    }

    public void setSubscriptionMinimumScorePolicy(BigInteger subscriptionMinimumScorePolicy) {
        this.subscriptionMinimumScorePolicy = subscriptionMinimumScorePolicy;
    }

    public BigInteger getStakeRatioPolicy() {
        return stakeRatioPolicy;
    }

    public void setStakeRatioPolicy(BigInteger stakeRatioPolicy) {
        this.stakeRatioPolicy = stakeRatioPolicy;
    }

    public BigInteger getSchedulerRewardRatioPolicy() {
        return schedulerRewardRatioPolicy;
    }

    public void setSchedulerRewardRatioPolicy(BigInteger schedulerRewardRatioPolicy) {
        this.schedulerRewardRatioPolicy = schedulerRewardRatioPolicy;
    }

}