package com.iexec.worker.actuator;

import com.iexec.worker.ethereum.TransactionStatus;

import java.math.BigInteger;

public interface Actuator {

    TransactionStatus subscribeToPool();

    TransactionStatus contribute(String workOrderId, String workerResult, BigInteger contributeV, String contributeR, String contributeS);

    TransactionStatus reveal(String workOrderId, String workerResult);

}
