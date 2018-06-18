package com.iexec.common.model;

import java.math.BigInteger;

public class StateHistoryModel {

    private BigInteger success;
    private BigInteger failure;

    public StateHistoryModel(BigInteger success, BigInteger failure) {
        this.success = success;
        this.failure = failure;
    }

    public BigInteger getSuccess() {
        return success;
    }

    public void setSuccess(BigInteger success) {
        this.success = success;
    }

    public BigInteger getFailure() {
        return failure;
    }

    public void setFailure(BigInteger failure) {
        this.failure = failure;
    }
}
