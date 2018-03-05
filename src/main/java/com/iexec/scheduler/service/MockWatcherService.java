package com.iexec.scheduler.service;

import com.iexec.scheduler.contracts.generated.IexecHub;
import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.helper.MockConfig;
import com.iexec.scheduler.helper.WorkerPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Hash;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.tx.Contract;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.iexec.scheduler.helper.Utils.hashResult;
import static org.web3j.rlp.RlpDecoder.OFFSET_SHORT_LIST;
import static org.web3j.tx.Contract.staticExtractEventParameters;

@Service
public class MockWatcherService {

    private static final Logger log = LoggerFactory.getLogger(MockWatcherService.class);
    private static final DefaultBlockParameterName END = DefaultBlockParameterName.LATEST;
    private final IexecHubService iexecHubService;
    private final WorkerPoolService workerPoolService;
    private final Web3j web3j;

    private String workerPoolAddress;

    private boolean workerSubscribed;


    @Value("${ethereum.startBlock}")
    private BigInteger startBlock;

    @Autowired
    private MockConfig mockConfig;

    @Autowired
    private WorkerPoolConfig poolConfig;


    @Autowired
    public MockWatcherService(Web3j web3j, IexecHubService iexecHubService, WorkerPoolService workerPoolService) {
        this.web3j = web3j;
        this.iexecHubService = iexecHubService;
        this.workerPoolService = workerPoolService;
    }

    @PostConstruct
    public void run() throws Exception {
        onWorkerPoolSetupCompleted();
    }

    private void onWorkerPoolSetupCompleted() {
        watchSubscriptionAndSetWorkerSubscribed();
        watchWorkOrderAndAcceptWorkOrder();
        watchWorkOrderAcceptedAndCallForContribution();
        watchContributeAndRevealConsensus();
        watchRevealAndFinalizeWork();
    }

    private void watchSubscriptionAndSetWorkerSubscribed() {
        log.info("SCHEDLR watching WorkerPoolSubscriptionEvent");
        iexecHubService.getIexecHub().workerPoolSubscriptionEventObservable(getStartBlock(), END)
                .subscribe(workerPoolSubscriptionEvent -> {
                    if (workerPoolSubscriptionEvent.workerPool.equals(workerPoolService.getWorkerPoolAddress())) {
                        log.warn("SCHEDLR received WorkerPoolSubscriptionEvent for worker " + workerPoolSubscriptionEvent.worker);
                        workerSubscribed = true;
                        //now clouduser able to createWorkOrder
                    }
                });
    }

    private void watchWorkOrderAndAcceptWorkOrder() {
        log.info("SCHEDLR watching WorkOrderEvent (auto accept)");
        iexecHubService.getIexecHub().workOrderEventObservable(getStartBlock(), END)
                .subscribe(workOrderEvent -> {
                    log.warn("SCHEDLR received WorkOrderEvent " + workOrderEvent.woid);
                    log.warn("SCHEDLR analysing asked workOrder");
                    log.warn("SCHEDLR accepting workOrder");

                    try {
                        iexecHubService.getIexecHub().acceptWorkOrder(workOrderEvent.woid, workOrderEvent.workerPool).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
    }

    private void watchWorkOrderAcceptedAndCallForContribution() {
        log.info("SCHEDLR watching WorkOrderAcceptedEvent (auto callForContribution)");
        workerPoolService.getWorkerPool().workOrderAcceptedEventObservable(getStartBlock(), END)
                .subscribe(workOrderAcceptedEvent -> {
                    log.warn("SCHEDLR received WorkOrderAcceptedEvent" + workOrderAcceptedEvent.woid);
                    log.warn("SCHEDLR choosing a random worker");
                    log.warn("SCHEDLR calling pool for contribution of worker1 " + mockConfig.getCallForContribution().getWorker());
                    try {
                        log.info(workerPoolService.getWorkerPool().callForContribution(workOrderAcceptedEvent.woid,
                                mockConfig.getCallForContribution().getWorker(),
                                mockConfig.getCallForContribution().getEnclaveChallenge()).send().getGasUsed().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void watchContributeAndRevealConsensus() {
        log.info("SCHEDLR watching ContributeEvent (auto revealConsensus)");
        workerPoolService.getWorkerPool().contributeEventObservable(getStartBlock(), END)
                .subscribe(contributeEvent -> {
                    log.warn("SCHEDLR received ContributeEvent " + contributeEvent.woid);
                    log.warn("SCHEDLR checking if consensus reached?");
                    log.warn("SCHEDLR found consensus reached");
                    log.warn("SCHEDLR reavealing consensus " + hashResult(mockConfig.getWorkerResult()));
                    byte[] consensus = Numeric.hexStringToByteArray(hashResult(mockConfig.getWorkerResult()));
                    try {
                        workerPoolService.getWorkerPool().revealConsensus(contributeEvent.woid, consensus).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void watchRevealAndFinalizeWork() {
        log.info("SCHEDLR watching RevealEvent (auto finalizeWork)");
        workerPoolService.getWorkerPool().revealEventObservable(getStartBlock(), END)
                .subscribe(revealEvent -> {
                    log.warn("SCHEDLR received RevealEvent ");
                    log.warn("SCHEDLR checking if reveal timeout reached?");
                    log.warn("SCHEDLR found reveal timeout reached");
                    log.warn("SCHEDLR finalazing task");
                    workerPoolService.getWorkerPool().finalizedWork(revealEvent.woid,
                            mockConfig.getFinalizeWork().getStdout(),
                            mockConfig.getFinalizeWork().getStderr(),
                            mockConfig.getFinalizeWork().getUri());//"aStdout", "aStderr", "anUri"
                });
    }

    public boolean createWorkOrder(String workerPool, String app, String dataset, String workOrderParam, BigInteger workReward, BigInteger askedTrust, Boolean dappCallback, String beneficiary) throws Exception {
        boolean gasOk = false;

        if (isWorkerSubscribed()) {
            TransactionReceipt tr = iexecHubService.getIexecHub().createWorkOrder(workerPool, app, dataset, workOrderParam, workReward, askedTrust, dappCallback, beneficiary).send();
            gasOk = !tr.getGasUsed().equals(Contract.GAS_LIMIT);
        }
        return gasOk;
    }


    public boolean isWorkerSubscribed() {
        return workerSubscribed;
    }

    private DefaultBlockParameter getStartBlock() {
        return DefaultBlockParameter.valueOf(startBlock);
    }


}
