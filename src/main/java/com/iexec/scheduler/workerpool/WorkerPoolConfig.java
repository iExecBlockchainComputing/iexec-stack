package com.iexec.scheduler.workerpool;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class WorkerPoolConfig {

    private List<String> list;
    private String address;
    private BigInteger mode;
    private String name;
    private BigInteger subscriptionLockStakePolicy;
    private BigInteger subscriptionMinimumStakePolicy;
    private BigInteger subscriptionMinimumScorePolicy;
    private BigInteger stakeRatioPolicy;
    private BigInteger schedulerRewardRatioPolicy;
    private BigInteger resultRetentionPolicy;

    public WorkerPoolConfig() {
        this.list = new ArrayList<>();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getList() {
        return this.list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public BigInteger getMode() {
        return mode;
    }

    public void setMode(BigInteger mode) {
        this.mode = mode;
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

    public BigInteger getResultRetentionPolicy() {
        return resultRetentionPolicy;
    }

    public void setResultRetentionPolicy(BigInteger resultRetentionPolicy) {
        this.resultRetentionPolicy = resultRetentionPolicy;
    }

}