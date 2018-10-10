package com.iexec.scheduler.actuator;

import com.iexec.common.contracts.generated.IexecHub;
import com.iexec.common.ethereum.TransactionStatus;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple8;

import java.math.BigInteger;
import java.util.List;

public interface Actuator {

    TransactionStatus allowWorkersToContribute(String workOrderId,
                                               List<String> workers,
                                               String enclaveChallenge);

    TransactionStatus depositRlc(BigInteger rlcToDeposit);

    TransactionStatus depositRlc();

    BigInteger createMarketOrder(BigInteger category, BigInteger trust, BigInteger value, BigInteger volume);

    TransactionStatus revealConsensus(String workOrderId, String hashResult);

    TransactionStatus reopen(String workOrderId);

    TransactionStatus finalizeWork(String workOrderId, String stdout, String stderr, String uri);

    TransactionStatus triggerWorkOrderCallback(String workOrderId, String stdout, String stderr, String uri);

    List<IexecHub.CreateCategoryEventResponse> getCategories();

    BigInteger getWorkerScore(String worker);
}
