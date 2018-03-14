package com.iexec.scheduler.mock;

import com.iexec.scheduler.contracts.generated.IexecHub;
import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.ethereum.CredentialsService;
import com.iexec.scheduler.ethereum.EthConfig;
import com.iexec.scheduler.ethereum.RlcService;
import com.iexec.scheduler.iexechub.IexecHubService;
import com.iexec.scheduler.marketplace.MarketOrderDirectionEnum;
import com.iexec.scheduler.marketplace.MarketplaceService;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple7;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static com.iexec.scheduler.ethereum.Utils.getStatus;
import static com.iexec.scheduler.ethereum.Utils.hashResult;

@Service
public class MockWatcherService {

    private static final Logger log = LoggerFactory.getLogger(MockWatcherService.class);
    private static final DefaultBlockParameterName END = DefaultBlockParameterName.LATEST;
    private final IexecHubService iexecHubService;
    private final WorkerPoolService workerPoolService;
    private final MarketplaceService marketplaceService;
    private final RlcService rlcService;
    private final CredentialsService credentialsService;
    private final MockConfig mockConfig;
    private final EthConfig ethConfig;
    private final byte[] EMPTY_BYTE = new byte[0];
    private boolean workerSubscribed;
    private Map<Tuple2<String, String>, byte[]> contributionMap = new HashMap<>();

    @Autowired
    public MockWatcherService(IexecHubService iexecHubService, WorkerPoolService workerPoolService,
                              MarketplaceService marketplaceService, RlcService rlcService, CredentialsService credentialsService,
                              MockConfig mockConfig, EthConfig ethConfig) {
        this.iexecHubService = iexecHubService;
        this.workerPoolService = workerPoolService;
        this.marketplaceService = marketplaceService;
        this.rlcService = rlcService;
        this.credentialsService = credentialsService;
        this.mockConfig = mockConfig;
        this.ethConfig = ethConfig;
    }

    @PostConstruct
    public void run() throws Exception {
        watchSubscriptionAndSetWorkerSubscribed();
        watchAskMarketOrderEmittedAndAnswerEmitWorkOrder();
        watchWorkOrderActivatedAndCallForContribution();
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
                        //TODO - emitMarketOrder if n workers are alive (not subscribed, means nothing)
                        try {
                            TransactionReceipt approveReceipt = rlcService.getRlc().approve(iexecHubService.getIexecHub().getContractAddress(), BigInteger.valueOf(100)).send();
                            log.info("SCHEDLR approve (emitMarketOrder) " + getStatus(approveReceipt));
                            TransactionReceipt depositReceipt = iexecHubService.getIexecHub().deposit(BigInteger.valueOf(30)).send();
                            log.info("SCHEDLR deposit (emitMarketOrder) " + getStatus(depositReceipt));

                            TransactionReceipt emitMarketOrderReceipt = marketplaceService.getMarketplace().emitMarketOrder(
                                    mockConfig.getEmitMarketOrder().getDirection(),
                                    mockConfig.getEmitMarketOrder().getCategory(),
                                    mockConfig.getEmitMarketOrder().getTrust(),
                                    mockConfig.getEmitMarketOrder().getValue(),
                                    workerPoolService.getWorkerPoolAddress(),
                                    mockConfig.getEmitMarketOrder().getVolume()
                            ).send();
                            log.info("SCHEDLR emitMarketOrder " + getStatus(emitMarketOrderReceipt));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void watchAskMarketOrderEmittedAndAnswerEmitWorkOrder() {
        // /!\ ALL THIS BLOCK SHOULD MADE BY IEXECCLOUDUSER
        log.info("CLDUSER watching marketOrderEmittedEvent (auto answerEmitWorkOrder)");
        marketplaceService.getMarketplace().marketOrderEmittedEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(marketOrderEmittedEvent -> {
                    log.info("SCHEDLR received marketOrderEmittedEvent " + marketOrderEmittedEvent.marketorderIdx);
                    //populate map and expose
                    try {
                        TransactionReceipt approveReceipt = rlcService.getRlc().approve(iexecHubService.getIexecHub().getContractAddress(), BigInteger.valueOf(100)).send();
                        log.info("SCHEDLR approve (answerEmitWorkOrder) " + getStatus(approveReceipt));
                        TransactionReceipt depositReceipt = iexecHubService.getIexecHub().deposit(BigInteger.valueOf(100)).send();
                        log.info("SCHEDLR deposit (answerEmitWorkOrder) " + getStatus(depositReceipt));

                        Tuple7 orderBook = marketplaceService.getMarketplace().m_orderBook(marketOrderEmittedEvent.marketorderIdx).send();
                        if (orderBook.getValue1().equals(MarketOrderDirectionEnum.ASK) &&
                                orderBook.getValue7().equals(workerPoolService.getWorkerPoolAddress())) {
                            TransactionReceipt answerEmitWorkOrderReceipt = iexecHubService.getIexecHub().answerEmitWorkOrder(marketOrderEmittedEvent.marketorderIdx,
                                    workerPoolService.getWorkerPoolAddress(),
                                    mockConfig.getAnswerEmitWorkOrder().getApp(),
                                    mockConfig.getAnswerEmitWorkOrder().getDataset(),
                                    mockConfig.getAnswerEmitWorkOrder().getParams(),
                                    mockConfig.getAnswerEmitWorkOrder().getCallback(),
                                    mockConfig.getAnswerEmitWorkOrder().getBeneficiary()
                            ).send();
                            log.info("SCHEDLR answerEmitWorkOrder " + getStatus(answerEmitWorkOrderReceipt));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
    }


    private void watchWorkOrderActivatedAndCallForContribution() {
        log.info("SCHEDLR watching workOrderActivatedEvent (auto callForContribution)");
        iexecHubService.getIexecHub().workOrderActivatedEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(workOrderActivatedEvent -> {
                    log.info("SCHEDLR received workOrderActivatedEvent " + workOrderActivatedEvent.woid);
                    setupForFutureContributions(workOrderActivatedEvent);
                    try {
                        TransactionReceipt callForContributionsReceipt = workerPoolService.getWorkerPool()
                                .callForContributions(workOrderActivatedEvent.woid,
                                        mockConfig.getCallForContribution().getWorkers(),
                                        mockConfig.getCallForContribution().getEnclaveChallenge()).send();
                        log.info("SCHEDLR callForContributions " + getStatus(callForContributionsReceipt)
                                + " of workers " + mockConfig.getCallForContribution().getWorkers().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void setupForFutureContributions(IexecHub.WorkOrderActivatedEventResponse workOrderActivatedEvent) {
        for (String worker : mockConfig.getCallForContribution().getWorkers()) {
            Tuple2<String, String> orderWorkerTuple = new Tuple2<>(workOrderActivatedEvent.woid, worker);
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
                    byte[] consensus = Numeric.hexStringToByteArray(hashResult(mockConfig.getWorkerResult()));
                    try {
                        TransactionReceipt revealConsensusReceipt = workerPoolService.getWorkerPool()
                                .revealConsensus(contributeEvent.woid, consensus).send();
                        log.info("SCHEDLR revealConsensus " + hashResult(mockConfig.getWorkerResult()) + " "
                                + getStatus(revealConsensusReceipt));
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
                    try {
                        TransactionReceipt finalizedWorkReceipt = workerPoolService.getWorkerPool().finalizedWork(revealEvent.woid,
                                mockConfig.getFinalizeWork().getStdout(),
                                mockConfig.getFinalizeWork().getStderr(),
                                mockConfig.getFinalizeWork().getUri()).send();
                        log.info("SCHEDLR finalize " + getStatus(finalizedWorkReceipt));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    /*
    public TransactionReceipt createWorkOrder(String workerPool, String app, String dataset, String workOrderParam, BigInteger workReward, BigInteger askedTrust, Boolean dappCallback, String beneficiary) throws Exception {
        TransactionReceipt tr = null;
        if (isWorkerSubscribed()) {
            tr = iexecHubService.getIexecHub().createWorkOrder(workerPool, app, dataset, workOrderParam, workReward, askedTrust, dappCallback, beneficiary).send();
        }
        return tr;
    }*/

    public boolean isWorkerSubscribed() {
        return workerSubscribed;
    }

}
