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

    BigInteger createMarketOrder(BigInteger category, BigInteger trust, BigInteger value, BigInteger volume);

    TransactionStatus revealConsensus(String workOrderId, String hashResult);

    TransactionStatus finalizeWork(String workOrderId, String stdout, String stderr, String uri);

    List<IexecHub.CreateCategoryEventResponse> getCategories();

    BigInteger getWorkerScore(String worker);

    Tuple2<BigInteger, BigInteger> getContributionHistory();

    BigInteger getSuccessContributionHistory();

    BigInteger getFailledContributionHistory();

    BigInteger getMarketOrdersCount();

    Tuple8<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, String> getMarketOrder(BigInteger marketorderIdx);

    Boolean existingMarketOrder(BigInteger marketorderIdx);

    BigInteger getMarketOrderValue(BigInteger marketorderIdx);

    BigInteger getMarketOrderCategory(BigInteger marketorderIdx);

    BigInteger getMarketOrderTrust(BigInteger marketorderIdx);

}
