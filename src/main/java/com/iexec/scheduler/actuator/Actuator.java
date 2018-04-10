package com.iexec.scheduler.actuator;

import com.iexec.common.contracts.generated.IexecHub;
import com.iexec.common.ethereum.TransactionStatus;

import java.math.BigInteger;
import java.util.List;

public interface Actuator {

    TransactionStatus allowWorkersToContribute(String workOrderId,
                                               List<String> workers,
                                               String enclaveChallenge);

    BigInteger createMarketOrder(BigInteger category, BigInteger trust, BigInteger value, BigInteger volume);

    TransactionStatus revealConsensus(String workOrderId, String hashResult);

    TransactionStatus finalizeWork(String workOrderId, String stdout, String stderr, String uri);

    List<IexecHub.CreateCategoryEventResponse> getCategories();
}
