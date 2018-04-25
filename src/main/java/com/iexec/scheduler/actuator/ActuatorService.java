package com.iexec.scheduler.actuator;

import com.iexec.common.contracts.generated.IexecHub;
import com.iexec.common.ethereum.RlcService;
import com.iexec.common.ethereum.TransactionStatus;
import com.iexec.common.marketplace.MarketOrderDirectionEnum;
import com.iexec.scheduler.iexechub.IexecHubService;
import com.iexec.scheduler.marketplace.MarketplaceService;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tuples.generated.Tuple8;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.iexec.common.ethereum.Utils.getStatus;

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
    public BigInteger createMarketOrder(BigInteger category, BigInteger trust, BigInteger value, BigInteger volume) {
        //TODO - createMarketOrder if n workers are alive (not subscribed, means nothing)
        try {
            TransactionReceipt approveReceipt = rlcService.getRlc().approve(iexecHubService.getIexecHub().getContractAddress(), value).send();
            //TODO ADD RATIO on approval value
            log.info("Approve for createMarketOrder [approveAmount:{}, transactionStatus:{}] ",
                    value, getStatus(approveReceipt));
            TransactionReceipt depositReceipt = iexecHubService.getIexecHub().deposit(value).send();
            log.info("Deposit for createMarketOrder [depositAmount:{}, transactionStatus:{}] ",
                    value, getStatus(depositReceipt));

            TransactionReceipt createMarketOrderReceipt = marketplaceService.getMarketplace().createMarketOrder(
                    MarketOrderDirectionEnum.ASK,
                    category,
                    trust,
                    value,
                    workerPoolService.getWorkerPoolConfig().getAddress(),
                    volume
            ).send();
            log.info("CreateMarketOrder [category:{}, trust:{}, value:{}, volume:{}, transactionStatus:{}] ",
                    category, trust, value, volume, getStatus(createMarketOrderReceipt));

            return marketplaceService.getMarketplace().getMarketOrderCreatedEvents(createMarketOrderReceipt).get(0).marketorderIdx;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO add closeMarketOrder function


    @Override
    public TransactionStatus allowWorkersToContribute(String workOrderId,
                                                      List<String> workers,
                                                      String enclaveChallenge) {
        try {
            TransactionReceipt allowWorkersToContributeReceipt = workerPoolService.getWorkerPool()
                    .allowWorkersToContribute(workOrderId, workers, enclaveChallenge).send();
            log.info("AllowWorkersToContribute [workOrderId:{}, workers:{}, enclaveChallenge:{}, transactionStatus:{}] ",
                    workOrderId, workers.toString(), enclaveChallenge, getStatus(allowWorkersToContributeReceipt));
            return getStatus(allowWorkersToContributeReceipt);
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
            log.info("RevealConsensus [hashResult:{}, transactionStatus:{}] ",
                    hashResult, getStatus(revealConsensusReceipt));
            return getStatus(revealConsensusReceipt);
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
            log.info("FinalizeWork [stdout:{}, stderr:{}, uri:{}, transactionStatus:{}] ",
                    stdout, stderr, uri, getStatus(finalizeWorkReceipt));
            return getStatus(finalizeWorkReceipt);
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


    @Override
    public Tuple2<BigInteger, BigInteger> getContributionHistory() {
        try {
            return iexecHubService.getIexecHub().m_contributionHistory().send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Tuple2(new BigInteger("-1"), new BigInteger("-1"));
    }


    @Override
    public BigInteger getSuccessContributionHistory() {
        Tuple2 result = getContributionHistory();
        return (BigInteger)result.getValue1();
    }

    @Override
    public BigInteger getFailledContributionHistory() {
        Tuple2 result = getContributionHistory();
        return (BigInteger)result.getValue2();
    }
    

    //Marketplace getters

    @Override
    public BigInteger getMarketOrdersCount() {
        try {
            return marketplaceService.getMarketplace().m_orderCount().send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BigInteger("-1");
    }

    @Override
    public Tuple8<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, String> getMarketOrder(BigInteger marketorderIdx) {
        try {
            return marketplaceService.getMarketplace().getMarketOrder(marketorderIdx).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Boolean existingMarketOrder(BigInteger marketorderIdx) {
        try {
            return marketplaceService.getMarketplace().existingMarketOrder(marketorderIdx).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public BigInteger getMarketOrderValue(BigInteger marketorderIdx) {
        try {
            return marketplaceService.getMarketplace().getMarketOrderValue(marketorderIdx).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BigInteger("-1");
    }

    @Override
    public BigInteger getMarketOrderCategory(BigInteger marketorderIdx) {
        try {
            return marketplaceService.getMarketplace().getMarketOrderCategory(marketorderIdx).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BigInteger("-1");
    }


    @Override
    public BigInteger getMarketOrderTrust(BigInteger marketorderIdx) {
        try {
            return marketplaceService.getMarketplace().getMarketOrderTrust(marketorderIdx).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BigInteger("-1");
    }


}
