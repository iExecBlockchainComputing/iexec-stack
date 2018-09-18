package com.iexec.common.model;

import org.web3j.utils.Numeric;

import java.math.BigInteger;

public class ContributionModel {

    private ContributionStatusEnum status;
    private byte[] resultHash;
    private byte[] resultSign;
    private String enclaveChallenge;
    private BigInteger score;
    private BigInteger weight;

    public ContributionModel(ContributionStatusEnum status, byte[] resultHash, byte[] resultSign, String enclaveChallenge, BigInteger score, BigInteger weight) {
        this.status = status;
        this.resultHash = resultHash;
        this.resultSign = resultSign;
        this.enclaveChallenge = enclaveChallenge;
        this.score = score;
        this.weight = weight;
    }

    public ContributionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ContributionStatusEnum status) {
        this.status = status;
    }

    public byte[] getResultHash() {
        return resultHash;
    }

    public void setResultHash(byte[] resultHash) {
        this.resultHash = resultHash;
    }

    public byte[] getResultSign() {
        return resultSign;
    }

    public void setResultSign(byte[] resultSign) {
        this.resultSign = resultSign;
    }

    public String getEnclaveChallenge() {
        return enclaveChallenge;
    }

    public void setEnclaveChallenge(String enclaveChallenge) {
        this.enclaveChallenge = enclaveChallenge;
    }

    public BigInteger getScore() {
        return score;
    }

    public void setScore(BigInteger score) {
        this.score = score;
    }

    public BigInteger getWeight() {
        return weight;
    }

    public void setWeight(BigInteger weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "ContributionModel{" +
                "status=" + status +
                ", resultHash=" + Numeric.toHexString(resultHash) +
                ", resultSign=" + Numeric.toHexString(resultSign) +
                ", enclaveChallenge='" + enclaveChallenge + '\'' +
                ", score=" + score +
                ", weight=" + weight +
                '}';
    }

}
