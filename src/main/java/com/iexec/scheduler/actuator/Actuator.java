package com.iexec.scheduler.actuator;

import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.ethereum.TransactionError;

import java.math.BigInteger;
import java.util.List;

public interface Actuator {

    TransactionError callForContributions(String woid,
                                          List<String> workers,
                                          String enclaveChallenge);

    TransactionError emitMarketOrder(BigInteger category, BigInteger trust, BigInteger value, BigInteger volume);
    TransactionError revealConsensus(WorkerPool.ContributeEventResponse contributeEvent, String hashResult);
    TransactionError finalizeWork(WorkerPool.RevealEventResponse revealEvent, String stdout, String stderr, String uri);
}
