package com.iexec.worker.workerpool;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class WorkerPoolPolicy {

    private String address;
    private String name;
    private BigInteger subscriptionLockStakePolicy;
    private BigInteger subscriptionMinimumStakePolicy;
    private BigInteger subscriptionMinimumScorePolicy;
    private BigInteger stakeRatioPolicy;
    private BigInteger schedulerRewardRatioPolicy;
    private BigInteger resultRetentionPolicy;
    private BigInteger mode;
    private List<String> list;

    public WorkerPoolPolicy() {
        list = new ArrayList<>();
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    @Override
    public String toString() {
        return "WorkerPoolPolicy{" +
                "address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", subscriptionLockStakePolicy=" + subscriptionLockStakePolicy +
                ", subscriptionMinimumStakePolicy=" + subscriptionMinimumStakePolicy +
                ", subscriptionMinimumScorePolicy=" + subscriptionMinimumScorePolicy +
                ", stakeRatioPolicy=" + stakeRatioPolicy +
                ", schedulerRewardRatioPolicy=" + schedulerRewardRatioPolicy +
                ", resultRetentionPolicy=" + resultRetentionPolicy +
                ", mode=" + mode +
                ", list=" + list +
                '}';
    }
}