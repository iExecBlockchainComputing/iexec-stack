package com.iexec.scheduler.mock;

import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.ethereum.EthConfig;
import com.iexec.scheduler.iexechub.IexecHubService;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static com.iexec.scheduler.ethereum.Utils.hashResult;

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
                        log.info("SCHEDLR received WorkerPoolSubscriptionEvent for worker " + workerPoolSubscriptionEvent.worker);
                        workerSubscribed = true;
                        //now clouduser able to createWorkOrder
                    }
                });
    }

    private void watchWorkOrderAndAcceptWorkOrder() {
        log.info("SCHEDLR watching WorkOrderEvent (auto accept)");
        iexecHubService.getIexecHub().workOrderEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(workOrderEvent -> {
                    log.info("SCHEDLR received WorkOrderEvent " + workOrderEvent.woid);
                    log.info("SCHEDLR analysing asked workOrder");
                    //TODO - Count alive workers before accepting WorkOrder (change isWorkerSubscribed)
                    log.info("SCHEDLR accepting workOrder");

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
                    log.info("SCHEDLR received WorkOrderAcceptedEvent" + workOrderAcceptedEvent.woid);
                    log.info("SCHEDLR calling pool for contribution of workers: " + mockConfig.getCallForContribution().getWorkers().toString());

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
                    log.info("SCHEDLR received ContributeEvent " + contributeEvent.woid + " of worker " + contributeEvent.worker);

                    addContributionToMap(contributeEvent);
                    if (!isConsensusReached(contributeEvent)) {
                        return;
                    }
                    log.info("SCHEDLR reavealing consensus " + hashResult(mockConfig.getWorkerResult()));
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
        log.info("SCHEDLR checking if consensus reached?");
        for (String worker : mockConfig.getCallForContribution().getWorkers()) {
            Tuple2<String, String> orderWorkerTuple = new Tuple2<>(contributeEvent.woid, worker);
            if (Arrays.areEqual(contributionMap.get(orderWorkerTuple), EMPTY_BYTE)) {
                return false;
            }
        }
        log.info("SCHEDLR found consensus reached");
        return true;
    }

    private void watchRevealAndFinalizeWork() {
        log.info("SCHEDLR watching RevealEvent (auto finalizeWork)");
        workerPoolService.getWorkerPool().revealEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(revealEvent -> {
                    log.info("SCHEDLR received RevealEvent: " + Numeric.toHexString(revealEvent.result));
                    log.info("SCHEDLR checking if reveal timeout reached?");
                    log.info("SCHEDLR found reveal timeout reached");
                    log.info("SCHEDLR finalazing task");
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

    public TransactionReceipt createWorkOrder(String workerPool, String app, String dataset, String workOrderParam, BigInteger workReward, BigInteger askedTrust, Boolean dappCallback, String beneficiary) throws Exception {
        TransactionReceipt tr = null;
        if (isWorkerSubscribed()) {
            tr = iexecHubService.getIexecHub().createWorkOrder(workerPool, app, dataset, workOrderParam, workReward, askedTrust, dappCallback, beneficiary).send();
        }
        return tr;
    }

    public boolean isWorkerSubscribed() {
        return workerSubscribed;
    }

}
