package com.iexec.scheduler.actuator;

import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.ethereum.TransactionStatus;

import java.math.BigInteger;
import java.util.List;

public interface Actuator {

    TransactionStatus allowWorkersToContribute(String workOrderId,
                                               List<String> workers,
                                               String enclaveChallenge);

    TransactionStatus createMarketOrder(BigInteger category, BigInteger trust, BigInteger value, BigInteger volume);

    TransactionStatus revealConsensus(String workOrderId, String hashResult);

    TransactionStatus finalizeWork(String workOrderId, String stdout, String stderr, String uri);
}
