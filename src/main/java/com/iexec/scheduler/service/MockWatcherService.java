package com.iexec.scheduler.service;

import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.helper.EthConfig;
import com.iexec.scheduler.helper.MockConfig;
import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static com.iexec.scheduler.helper.Utils.hashResult;

@Service
public class MockWatcherService {

    private static final Logger log = LoggerFactory.getLogger(MockWatcherService.class);
    private static final DefaultBlockParameterName END = DefaultBlockParameterName.LATEST;
    private final IexecHubService iexecHubService;
    private final WorkerPoolService workerPoolService;
    private final MockConfig mockConfig;
    private final EthConfig ethConfig;
    private final byte[] EMPTY_BYTE = new byte[0];
    private boolean workerSubscribed;
    private Map<Tuple2<String, String>, byte[]> contributionMap = new HashMap<>();
    ;

    @Autowired
    public MockWatcherService(IexecHubService iexecHubService, WorkerPoolService workerPoolService,
                              MockConfig mockConfig, EthConfig ethConfig) {
        this.iexecHubService = iexecHubService;
        this.workerPoolService = workerPoolService;
        this.mockConfig = mockConfig;
        this.ethConfig = ethConfig;
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
        iexecHubService.getIexecHub().workerPoolSubscriptionEventObservable(ethConfig.getStartBlockParameter(), END)
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
        iexecHubService.getIexecHub().workOrderEventObservable(ethConfig.getStartBlockParameter(), END)
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
        workerPoolService.getWorkerPool().workOrderAcceptedEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(workOrderAcceptedEvent -> {
                    log.warn("SCHEDLR received WorkOrderAcceptedEvent" + workOrderAcceptedEvent.woid);
                    log.warn("SCHEDLR calling pool for contribution of workers: " + mockConfig.getCallForContribution().getWorkers().toString());

                    setupForFutureContributions(workOrderAcceptedEvent);
                    try {
                        log.info(workerPoolService.getWorkerPool().callForContributions(workOrderAcceptedEvent.woid,
                                mockConfig.getCallForContribution().getWorkers(),
                                mockConfig.getCallForContribution().getEnclaveChallenge()).send().getGasUsed().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void setupForFutureContributions(WorkerPool.WorkOrderAcceptedEventResponse workOrderAcceptedEvent) {
        for (String worker : mockConfig.getCallForContribution().getWorkers()) {
            Tuple2<String, String> orderWorkerTuple = new Tuple2<>(workOrderAcceptedEvent.woid, worker);
            contributionMap.put(orderWorkerTuple, EMPTY_BYTE);
        }
    }

    private void watchContributeAndRevealConsensus() {
        log.info("SCHEDLR watching ContributeEvent (auto revealConsensus)");
        workerPoolService.getWorkerPool().contributeEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(contributeEvent -> {
                    log.warn("SCHEDLR received ContributeEvent " + contributeEvent.woid + " of worker " + contributeEvent.worker);

                    addContributionToMap(contributeEvent);
                    if (!isConsensusReached(contributeEvent)) {
                        return;
                    }
                    log.warn("SCHEDLR reavealing consensus " + hashResult(mockConfig.getWorkerResult()));
                    byte[] consensus = Numeric.hexStringToByteArray(hashResult(mockConfig.getWorkerResult()));
                    try {
                        workerPoolService.getWorkerPool().revealConsensus(contributeEvent.woid, consensus).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void addContributionToMap(WorkerPool.ContributeEventResponse contributeEvent) {
        Tuple2<String, String> orderWorkerTuple = new Tuple2<>(contributeEvent.woid, contributeEvent.worker);
        if (contributionMap.containsKey(orderWorkerTuple)) {
            contributionMap.replace(orderWorkerTuple, contributeEvent.resultHash);
        }
    }

    private boolean isConsensusReached(WorkerPool.ContributeEventResponse contributeEvent) {
        log.warn("SCHEDLR checking if consensus reached?");
        for (String worker : mockConfig.getCallForContribution().getWorkers()) {
            Tuple2<String, String> orderWorkerTuple = new Tuple2<>(contributeEvent.woid, worker);
            if (Arrays.areEqual(contributionMap.get(orderWorkerTuple), EMPTY_BYTE)) {
                return false;
            }
        }
        log.warn("SCHEDLR found consensus reached");
        return true;
    }

    private void watchRevealAndFinalizeWork() {
        log.info("SCHEDLR watching RevealEvent (auto finalizeWork)");
        workerPoolService.getWorkerPool().revealEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(revealEvent -> {
                    log.warn("SCHEDLR received RevealEvent: " + Numeric.toHexString(revealEvent.result));
                    log.warn("SCHEDLR checking if reveal timeout reached?");
                    log.warn("SCHEDLR found reveal timeout reached");
                    log.warn("SCHEDLR finalazing task");
                    try {
                        workerPoolService.getWorkerPool().finalizedWork(revealEvent.woid,
                                mockConfig.getFinalizeWork().getStdout(),
                                mockConfig.getFinalizeWork().getStderr(),
                                mockConfig.getFinalizeWork().getUri()).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

}
