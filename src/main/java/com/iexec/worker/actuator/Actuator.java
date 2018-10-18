package com.iexec.worker.actuator;

import com.iexec.common.ethereum.TransactionStatus;

import java.math.BigInteger;

public interface Actuator {

    TransactionStatus depositRlc(BigInteger rlcDepositRequested);

    TransactionStatus depositRlc();

    TransactionStatus subscribeToPool();

    TransactionStatus unsubscribeFromPool();

    TransactionStatus contribute(String workOrderId, String hashResult, String signResult, BigInteger contributeV, String contributeR, String contributeS);

    TransactionStatus reveal(String workOrderId, String workerResult);

}
