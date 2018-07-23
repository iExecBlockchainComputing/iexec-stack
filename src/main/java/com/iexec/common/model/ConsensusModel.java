package com.iexec.common.model;

import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;

public class ConsensusModel {

    private BigInteger poolReward;
    private BigInteger stakeAmount;
    private byte[] consensus;
    private BigInteger revealDate;
    private BigInteger revealCounter;
    private BigInteger consensusTimeout;
    private BigInteger winnerCount;
    private String workerpoolOwner;

    public ConsensusModel(BigInteger poolReward, BigInteger stakeAmount, byte[] consensus, BigInteger revealDate, BigInteger revealCounter, BigInteger consensusTimeout, BigInteger winnerCount, String workerpoolOwner) {
        this.poolReward = poolReward;
        this.stakeAmount = stakeAmount;
        this.consensus = consensus;
        this.revealDate = revealDate;
        this.revealCounter = revealCounter;
        this.consensusTimeout = consensusTimeout;
        this.winnerCount = winnerCount;
        this.workerpoolOwner = workerpoolOwner;
    }

    public BigInteger getPoolReward() {
        return poolReward;
    }

    public void setPoolReward(BigInteger poolReward) {
        this.poolReward = poolReward;
    }

    public BigInteger getStakeAmount() {
        return stakeAmount;
    }

    public void setStakeAmount(BigInteger stakeAmount) {
        this.stakeAmount = stakeAmount;
    }

    public byte[] getConsensus() {
        return consensus;
    }

    public void setConsensus(byte[] consensus) {
        this.consensus = consensus;
    }

    public BigInteger getRevealDate() {
        return revealDate;
    }

    public void setRevealDate(BigInteger revealDate) {
        this.revealDate = revealDate;
    }

    public BigInteger getRevealCounter() {
        return revealCounter;
    }

    public void setRevealCounter(BigInteger revealCounter) {
        this.revealCounter = revealCounter;
    }

    public BigInteger getConsensusTimeout() {
        return consensusTimeout;
    }

    public void setConsensusTimeout(BigInteger consensusTimeout) {
        this.consensusTimeout = consensusTimeout;
    }

    public BigInteger getWinnerCount() {
        return winnerCount;
    }

    public void setWinnerCount(BigInteger winnerCount) {
        this.winnerCount = winnerCount;
    }

    public String getWorkerpoolOwner() {
        return workerpoolOwner;
    }

    public void setWorkerpoolOwner(String workerpoolOwner) {
        this.workerpoolOwner = workerpoolOwner;
    }

    @Override
    public String toString() {
        return "ConsensusModel{" +
                "poolReward=" + poolReward +
                ", stakeAmount=" + stakeAmount +
                ", consensus=" + Numeric.toHexString(consensus) +
                ", revealDate=" + revealDate +
                ", revealCounter=" + revealCounter +
                ", consensusTimeout=" + consensusTimeout +
                ", winnerCount=" + winnerCount +
                ", workerpoolOwner='" + workerpoolOwner + '\'' +
                '}';
    }
}
