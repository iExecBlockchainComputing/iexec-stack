package com.iexec.scheduler.actuator;

import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.ethereum.RlcService;
import com.iexec.scheduler.ethereum.TransactionError;
import com.iexec.scheduler.iexechub.IexecHubService;
import com.iexec.scheduler.marketplace.MarketplaceService;
import com.iexec.scheduler.mock.MockConfig;
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

@Service
public class ActuatorService implements Actuator {

    private static final Logger log = LoggerFactory.getLogger(ActuatorService.class);
    private final IexecHubService iexecHubService;
    private final WorkerPoolService workerPoolService;
    private final MarketplaceService marketplaceService;
    private final RlcService rlcService;
    private final MockConfig mockConfig;

    @Autowired
    public ActuatorService(IexecHubService iexecHubService, WorkerPoolService workerPoolService,
                           MarketplaceService marketplaceService, RlcService rlcService, MockConfig mockConfig) {
        this.iexecHubService = iexecHubService;
        this.workerPoolService = workerPoolService;
        this.marketplaceService = marketplaceService;
        this.rlcService = rlcService;
        this.mockConfig = mockConfig;
    }

    @Override
    public TransactionError emitMarketOrder(BigInteger category, BigInteger trust, BigInteger value, BigInteger volume) {
        //TODO - emitMarketOrder if n workers are alive (not subscribed, means nothing)
        try {
            Float deposit = (workerPoolService.getPoolConfig().getStakeRatioPolicy().floatValue() / 100) * mockConfig.getEmitMarketOrder().getValue().floatValue();//(30/100)*100
            BigInteger depositBig = BigDecimal.valueOf(deposit).toBigInteger();
            TransactionReceipt approveReceipt = rlcService.getRlc().approve(iexecHubService.getIexecHub().getContractAddress(), BigInteger.valueOf(100)).send();
            log.info("SCHEDLR approve (emitMarketOrder) " + 100 + " " + getStatus(approveReceipt));
            TransactionReceipt depositReceipt = iexecHubService.getIexecHub().deposit(depositBig).send();
            log.info("SCHEDLR deposit (emitMarketOrder) " + depositBig + " " + getStatus(depositReceipt));

            TransactionReceipt emitMarketOrderReceipt = marketplaceService.getMarketplace().emitMarketOrder(
                    mockConfig.getEmitMarketOrder().getDirection(),
                    category,
                    trust,
                    value,
                    workerPoolService.getPoolConfig().getAddress(),
                    volume
            ).send();
            log.info("SCHEDLR emitMarketOrder " + getStatus(emitMarketOrderReceipt));
            return getStatus(emitMarketOrderReceipt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionError.FAILURE;
    }


    @Override
    public TransactionError callForContributions(String woid,
                                                 List<String> workers,
                                                 String enclaveChallenge) {
        try {
            TransactionReceipt callForContributionsReceipt = workerPoolService.getWorkerPool()
                    .callForContributions(woid, workers, enclaveChallenge).send();
            log.info("SCHEDLR callForContributions " + getStatus(callForContributionsReceipt)
                    + " of workers " + workers.toString());
            return getStatus(callForContributionsReceipt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionError.FAILURE;
    }


    @Override
    public TransactionError revealConsensus(WorkerPool.ContributeEventResponse contributeEvent, String hashResult) {
        byte[] consensus = Numeric.hexStringToByteArray(hashResult);
        try {
            TransactionReceipt revealConsensusReceipt = workerPoolService.getWorkerPool()
                    .revealConsensus(contributeEvent.woid, consensus).send();
            log.info("SCHEDLR revealConsensus " + hashResult + " "
                    + getStatus(revealConsensusReceipt));
            return getStatus(revealConsensusReceipt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionError.FAILURE;
    }

    @Override
    public TransactionError finalizeWork(WorkerPool.RevealEventResponse revealEvent, String stdout, String stderr, String uri) {
        log.info("SCHEDLR found reveal timeout reached");
        try {
            TransactionReceipt finalizedWorkReceipt = workerPoolService.getWorkerPool().finalizedWork(revealEvent.woid,
                    stdout,
                    stderr,
                    uri).send();
            log.info("SCHEDLR finalize " + getStatus(finalizedWorkReceipt));
            return getStatus(finalizedWorkReceipt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionError.FAILURE;
    }


}
