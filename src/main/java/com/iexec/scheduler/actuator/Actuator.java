package com.iexec.scheduler.actuator;

import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.ethereum.TransactionStatus;

import java.math.BigInteger;
import java.util.List;

public interface Actuator {

    TransactionStatus callForContributions(String workOrderId,
                                           List<String> workers,
                                           String enclaveChallenge);

    TransactionStatus emitMarketOrder(BigInteger category, BigInteger trust, BigInteger value, BigInteger volume);
    TransactionStatus revealConsensus(WorkerPool.ContributeEventResponse contributeEvent, String hashResult);
    TransactionStatus finalizeWork(WorkerPool.RevealEventResponse revealEvent, String stdout, String stderr, String uri);
}
