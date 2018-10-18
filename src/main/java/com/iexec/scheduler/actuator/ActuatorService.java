package com.iexec.scheduler.actuator;

import com.iexec.common.contracts.generated.IexecHub;
import com.iexec.common.contracts.generated.Marketplace;
import com.iexec.common.contracts.generated.WorkerPool;
import com.iexec.common.ethereum.IexecConfigurationService;
import com.iexec.common.ethereum.RlcService;
import com.iexec.common.ethereum.TransactionStatus;
import com.iexec.common.ethereum.Utils;
import com.iexec.common.marketplace.MarketOrderDirectionEnum;
import com.iexec.scheduler.iexechub.IexecHubService;
import com.iexec.scheduler.marketplace.MarketplaceService;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.iexec.common.ethereum.Utils.getTransactionStatusFromEvents;

public class ActuatorService implements Actuator {

    private static final Logger log = LoggerFactory.getLogger(ActuatorService.class);
    private final static IexecHubService iexecHubService = IexecHubService.getInstance();
    private final static WorkerPoolService workerPoolService = WorkerPoolService.getInstance();
    private final static MarketplaceService marketplaceService = MarketplaceService.getInstance();
    private final static RlcService rlcService = RlcService.getInstance();
    private static ActuatorService instance;

    private ActuatorService() {
    }

    public static ActuatorService getInstance() {
        if (instance == null) {
            instance = new ActuatorService();
        }
        return instance;
    }

    @Override
    public TransactionStatus depositRlc(BigInteger rlcDepositRequested) {
        return Utils.depositRlc(rlcDepositRequested, rlcService.getRlc(), iexecHubService.getIexecHub(), log);
    }

    @Override
    public TransactionStatus depositRlc() {
        return depositRlc(IexecConfigurationService.getInstance().getWalletConfig().getRlcDeposit());
    }

    @Override
    public BigInteger createMarketOrder(BigInteger category, BigInteger trust, BigInteger value, BigInteger volume) {
        //TODO - createMarketOrder if n workers are alive (not subscribed, means nothing)
        try {
            TransactionReceipt createMarketOrderReceipt = marketplaceService.getMarketplace().createMarketOrder(
                    MarketOrderDirectionEnum.ASK,
                    category,
                    trust,
                    value,
                    workerPoolService.getWorkerPoolConfig().getAddress(),
                    volume
            ).send();
            List<Marketplace.MarketOrderCreatedEventResponse> marketOrderCreatedEvents = marketplaceService.getMarketplace().getMarketOrderCreatedEvents(createMarketOrderReceipt);
            log.info("CreateMarketOrder [category:{}, trust:{}, value:{}, volume:{}, marketorderIdx:{}, transactionHash:{}, transactionStatus:{}] ",
                    category, trust, value, volume, marketOrderCreatedEvents.get(0).marketorderIdx, createMarketOrderReceipt.getTransactionHash(), getTransactionStatusFromEvents(marketOrderCreatedEvents));
            return marketOrderCreatedEvents.get(0).marketorderIdx;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public TransactionStatus allowWorkersToContribute(String workOrderId,
                                                      List<String> workers,
                                                      String enclaveChallenge) {
        try {
            TransactionReceipt allowWorkersToContributeReceipt = workerPoolService.getWorkerPool()
                    .allowWorkersToContribute(workOrderId, workers, enclaveChallenge).send();
            List<WorkerPool.AllowWorkerToContributeEventResponse> allowWorkerToContributeEvents = workerPoolService.getWorkerPool().getAllowWorkerToContributeEvents(allowWorkersToContributeReceipt);
            log.info("AllowWorkersToContribute [workOrderId:{}, workers:{}, enclaveChallenge:{}, transactionHash:{}, transactionStatus:{}] ",
                    workOrderId, workers.toString(), enclaveChallenge, allowWorkersToContributeReceipt.getTransactionHash(), getTransactionStatusFromEvents(allowWorkerToContributeEvents));
            return getTransactionStatusFromEvents(allowWorkerToContributeEvents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }


    @Override
    public TransactionStatus revealConsensus(String workOrderId, String hashResult) {
        byte[] consensus = Numeric.hexStringToByteArray(hashResult);
        try {
            TransactionReceipt revealConsensusReceipt = workerPoolService.getWorkerPool()
                    .revealConsensus(workOrderId, consensus).send();
            List<WorkerPool.RevealConsensusEventResponse> revealConsensusEvents = workerPoolService.getWorkerPool().getRevealConsensusEvents(revealConsensusReceipt);
            log.info("RevealConsensus [workOrderId:{}, hashResult:{}, transactionHash:{}, transactionStatus:{}] ",
                    workOrderId, hashResult, revealConsensusReceipt.getTransactionHash(), getTransactionStatusFromEvents(revealConsensusEvents));
            return getTransactionStatusFromEvents(revealConsensusEvents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }

    @Override
    public TransactionStatus reopen(String workOrderId) {
        try {
            TransactionReceipt reopenReceipt = workerPoolService.getWorkerPool().reopen(workOrderId).send();
            List<WorkerPool.ReopenEventResponse> reopenEvents = workerPoolService.getWorkerPool().getReopenEvents(reopenReceipt);
            log.info("Reopen [workOrderId:{}, transactionHash:{}, transactionStatus:{}] ",
                    workOrderId, reopenReceipt.getTransactionHash(), getTransactionStatusFromEvents(reopenEvents));
            return getTransactionStatusFromEvents(reopenEvents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }

    @Override
    public TransactionStatus finalizeWork(String workOrderId, String stdout, String stderr, String uri) {
        try {
            TransactionReceipt finalizeWorkReceipt = workerPoolService.getWorkerPool().finalizeWork(workOrderId,
                    stdout,
                    stderr,
                    uri).send();
            List<WorkerPool.FinalizeWorkEventResponse> finalizeWorkEvents = workerPoolService.getWorkerPool().getFinalizeWorkEvents(finalizeWorkReceipt);
            log.info("FinalizeWork [workOrderId:{}, stdout:{}, stderr:{}, uri:{}, transactionHash:{}, transactionStatus:{}] ",
                    workOrderId, stdout, stderr, uri, finalizeWorkReceipt.getTransactionHash(), getTransactionStatusFromEvents(finalizeWorkEvents));
            return getTransactionStatusFromEvents(finalizeWorkEvents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }

    @Override
    public TransactionStatus triggerWorkOrderCallback(String workOrderId, String stdout, String stderr, String uri) {
        try {
            TransactionReceipt triggerCallbackReceipt = marketplaceService.getMarketplace().workOrderCallback(workOrderId,
                    stdout,
                    stderr,
                    uri).send();
            List<Marketplace.WorkOrderCallbackProofEventResponse> triggerCallbackEvents = marketplaceService.getMarketplace().getWorkOrderCallbackProofEvents(triggerCallbackReceipt);
            log.info("TriggerWorkOrderCallback [workOrderId:{}, stdout:{}, stderr:{}, uri:{}, transactionHash:{}, transactionStatus:{}] ",
                    workOrderId, stdout, stderr, uri, triggerCallbackReceipt.getTransactionHash(), getTransactionStatusFromEvents(triggerCallbackEvents));
            return getTransactionStatusFromEvents(triggerCallbackEvents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }

    @Override
    public List<IexecHub.CreateCategoryEventResponse> getCategories() {
        List<IexecHub.CreateCategoryEventResponse> categoryEvents = new ArrayList<>();
        try {
            for (int i = 1; i < iexecHubService.getIexecHub().m_categoriesCount().send().intValue() + 1; i++) {
                Tuple4<BigInteger, String, String, BigInteger> category = iexecHubService.getIexecHub().getCategory(new BigInteger(String.valueOf(i))).send();
                IexecHub.CreateCategoryEventResponse categoryEvent = new IexecHub.CreateCategoryEventResponse();
                categoryEvent.catid = category.getValue1();
                categoryEvent.name = category.getValue2();
                categoryEvent.description = category.getValue3();
                categoryEvent.workClockTimeRef = category.getValue4();
                categoryEvents.add(categoryEvent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categoryEvents;
    }


    //IexecHub getters

    @Override
    public BigInteger getWorkerScore(String worker) {
        try {
            return iexecHubService.getIexecHub().getWorkerScore(worker).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BigInteger("-1");
    }

}
