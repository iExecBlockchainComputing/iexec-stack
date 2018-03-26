package com.iexec.scheduler.actuator;

import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.ethereum.ConfigurationService;
import com.iexec.scheduler.ethereum.CredentialsService;
import com.iexec.scheduler.ethereum.RlcService;
import com.iexec.scheduler.ethereum.TransactionStatus;
import com.iexec.scheduler.iexechub.IexecHubService;
import com.iexec.scheduler.marketplace.MarketOrderDirectionEnum;
import com.iexec.scheduler.marketplace.MarketplaceService;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.iexec.scheduler.ethereum.Utils.getStatus;

public class ActuatorService implements Actuator {

    private static final Logger log = LoggerFactory.getLogger(ActuatorService.class);
    private static ActuatorService instance;
    private final static IexecHubService iexecHubService = IexecHubService.getInstance();
    private final static WorkerPoolService workerPoolService = WorkerPoolService.getInstance();
    private final static MarketplaceService marketplaceService = MarketplaceService.getInstance();
    private final static RlcService rlcService = RlcService.getInstance();

    public static ActuatorService getInstance() {
        if (instance==null){
            instance = new ActuatorService();
        }
        return instance;
    }

    private ActuatorService() {
    }

    @Override
    public TransactionStatus emitMarketOrder(BigInteger category, BigInteger trust, BigInteger value, BigInteger volume) {
        //TODO - emitMarketOrder if n workers are alive (not subscribed, means nothing)
        try {
            Float deposit = (workerPoolService.getWorkerPoolConfig().getStakeRatioPolicy().floatValue() / 100) * value.floatValue();//(30/100)*100
            BigInteger depositAmount = BigDecimal.valueOf(deposit).toBigInteger();
            BigInteger approveAmount = BigInteger.valueOf(100);//should be the same than deposit, Poco needs changes
            TransactionReceipt approveReceipt = rlcService.getRlc().approve(iexecHubService.getIexecHub().getContractAddress(), approveAmount).send();
            log.info("Approve for emitMarketOrder [approveAmount:{}, transactionStatus:{}] ",
                    approveAmount, getStatus(approveReceipt));
            TransactionReceipt depositReceipt = iexecHubService.getIexecHub().deposit(depositAmount).send();
            log.info("Deposit for emitMarketOrder [depositAmount:{}, transactionStatus:{}] ",
                    depositAmount, getStatus(depositReceipt));

            TransactionReceipt emitMarketOrderReceipt = marketplaceService.getMarketplace().emitMarketOrder(
                    MarketOrderDirectionEnum.ASK,
                    category,
                    trust,
                    value,
                    workerPoolService.getWorkerPoolConfig().getAddress(),
                    volume
            ).send();
            log.info("EmitMarketOrder [category:{}, trust:{}, value:{}, volume:{}, transactionStatus:{}] ",
                    category, trust, value, volume, getStatus(emitMarketOrderReceipt));

            return getStatus(emitMarketOrderReceipt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }


    @Override
    public TransactionStatus callForContributions(String workOrderId,
                                                  List<String> workers,
                                                  String enclaveChallenge) {
        try {
            TransactionReceipt callForContributionsReceipt = workerPoolService.getWorkerPool()
                    .callForContributions(workOrderId, workers, enclaveChallenge).send();
            log.info("CallForContributions [workOrderId:{}, workers:{}, enclaveChallenge:{}, transactionStatus:{}] ",
                    workOrderId, workers.toString(), enclaveChallenge, getStatus(callForContributionsReceipt));
            return getStatus(callForContributionsReceipt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }


    @Override
    public TransactionStatus revealConsensus(WorkerPool.ContributeEventResponse contributeEvent, String hashResult) {
        byte[] consensus = Numeric.hexStringToByteArray(hashResult);
        try {
            TransactionReceipt revealConsensusReceipt = workerPoolService.getWorkerPool()
                    .revealConsensus(contributeEvent.woid, consensus).send();
            log.info("RevealConsensus [hashResult:{}, transactionStatus:{}] ",
                    hashResult, getStatus(revealConsensusReceipt));
            return getStatus(revealConsensusReceipt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }

    @Override
    public TransactionStatus finalizeWork(WorkerPool.RevealEventResponse revealEvent, String stdout, String stderr, String uri) {
        try {
            TransactionReceipt finalizedWorkReceipt = workerPoolService.getWorkerPool().finalizedWork(revealEvent.woid,
                    stdout,
                    stderr,
                    uri).send();
            log.info("FinalizeWork [stdout:{}, stderr:{}, uri:{}, transactionStatus:{}] ",
                    stdout, stderr, uri, getStatus(finalizedWorkReceipt));
            return getStatus(finalizedWorkReceipt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }

}
