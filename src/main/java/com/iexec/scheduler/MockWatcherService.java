package com.iexec.scheduler;

import com.iexec.scheduler.contracts.generated.AuthorizedList;
import com.iexec.scheduler.contracts.generated.IexecHub;
import com.iexec.scheduler.contracts.generated.WorkerPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;

import static com.iexec.scheduler.Utils.asciiToHex;
import static com.iexec.scheduler.Utils.hashResult;
import static com.iexec.scheduler.Utils.signByteResult;

@Service
public class MockWatcherService {

    private static final Logger log = LoggerFactory.getLogger(MockWatcherService.class);

    private static final DefaultBlockParameterName START = DefaultBlockParameterName.EARLIEST;
    private static final DefaultBlockParameterName END = DefaultBlockParameterName.LATEST;
    private static final BigInteger BLACKLIST = BigInteger.ONE;
    private static final String WORKER_RESULT = "iExec the wanderer";

    private Web3j web3j;
    private Credentials schedulerCredentials;
    private Credentials workerCredentials;
    private IexecHub iexecHubForScheduler;
    private IexecHub iexecHubForWorker;
    private String workerPoolAddress;
    private WorkerPool workerPoolForScheduler;
    private WorkerPool workerPoolForWorker;
    private String workerPoolName;
    private boolean workerSubscribed;

    @Value("${ethereum.address.iexecHub}")
    private String iexecHubAddress;

    @Value("${wallet.folder}")
    private String walletFolder;

    @Value("${scheduler.address}")
    private String schedulerAddress;

    @Value("${scheduler.wallet.filename}")
    private String schedulerWalletFilename;

    @Value("${scheduler.wallet.password}")
    private String schedulerWalletPassword;

    @Value("${worker.address}")
    private String worker;

    @Value("${worker.wallet.filename}")
    private String workerWalletFilename;

    @Value("${worker.wallet.password}")
    private String workerWalletPassword;


    @Autowired
    public MockWatcherService(Web3j web3j) {
        this.web3j = web3j;
    }

    @PostConstruct
    public void run() throws Exception {

        init();
        createWorkerPool();
        changeWorkerPoolPolicy();
        watchSubscriptionAndSetWorkerSubscribed();
        watchWorkOrderAndAcceptWorkOrder();
        watchWorkOrderAcceptedAndCallForContribution();
        watchCallForContributionAndContribute();
        watchContributeAndRevealConsensus();
        watchRevealConsensusAndReveal();
        watchRevealAndFinalizeWork();

    }

    private void init() throws IOException, CipherException {
        log.info("Connected to Ethereum client version: " + web3j.web3ClientVersion().send().getWeb3ClientVersion());

        log.info("Loading credentials");
        schedulerCredentials = WalletUtils.loadCredentials(schedulerWalletPassword, walletFolder + "/" + schedulerWalletFilename);
        workerCredentials = WalletUtils.loadCredentials(workerWalletPassword, walletFolder + "/" + workerWalletFilename);

        log.info("Loading smart contracts");
        iexecHubForScheduler = IexecHub.load(
                iexecHubAddress, web3j, schedulerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        iexecHubForWorker = IexecHub.load(
                iexecHubAddress, web3j, workerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    private void createWorkerPool() throws Exception {
        log.info("createWorkerPool");
        workerPoolName = "myWorkerPool-" + System.currentTimeMillis();
        log.info("SCHEDLR creating workerPool: " + workerPoolName);
        iexecHubForScheduler.createWorkerPool(workerPoolName, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO).send();
    }

    private void changeWorkerPoolPolicy() {
        log.info("changeWorkerPoolPolicy");
        iexecHubForScheduler.createWorkerPoolEventObservable(START, END)
                .subscribe(createWorkerPoolEvent -> {
                    if (createWorkerPoolEvent.workerPoolName.equals(workerPoolName)) {
                        workerPoolAddress = createWorkerPoolEvent.workerPool;
                        log.warn("SCHEDLR received createWorkerPoolEvent " + createWorkerPoolEvent.workerPoolName + ":" + workerPoolAddress);
                        workerPoolForScheduler = WorkerPool.load(
                                workerPoolAddress, web3j, schedulerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                        workerPoolForWorker = WorkerPool.load(
                                workerPoolAddress, web3j, workerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

                        try {
                            String m_workersAuthorizedListAddress = workerPoolForScheduler.m_workersAuthorizedListAddress().send();

                            AuthorizedList authorizedList = AuthorizedList.load(
                                    m_workersAuthorizedListAddress, web3j, schedulerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

                            authorizedList.changeListPolicy(BLACKLIST).send();
                            subscribeToWorkerPool(authorizedList);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void subscribeToWorkerPool(AuthorizedList authorizedList) {
        log.info("subscribeToWorkerPool");
        authorizedList.policyChangeEventObservable(START, END)
                .subscribe(policyChangeEvent -> {
                    if (workerPoolForWorker != null) {
                        log.info("SCHEDLR received policyChangeEvent on workerpool from " + policyChangeEvent.oldPolicy + " to " + policyChangeEvent.newPolicy);

                        try {
                            log.info("WORKER1 subscribing to workerPool");
                            log.info(workerPoolForWorker.subscribeToPool().send().getGasUsed().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void watchSubscriptionAndSetWorkerSubscribed() {
        log.info("watchSubscriptionAndSetWorkerSubscribed");
        iexecHubForWorker.workerPoolSubscriptionEventObservable(START, END)
                .subscribe(workerPoolSubscriptionEvent -> {
                    if (workerPoolSubscriptionEvent.workerPool.equals(workerPoolAddress)) {
                        log.warn("WORKER1 received workerPoolSubscriptionEvent " + workerPoolSubscriptionEvent.worker);
                        workerSubscribed = true;
                        //createWorkOrder
                    }
                });
    }

    private void watchWorkOrderAndAcceptWorkOrder() {
        log.info("watchWorkOrderAndAcceptWorkOrder");
        iexecHubForScheduler.workOrderEventObservable(START, END)
                .subscribe(workOrderEvent -> {
                    log.warn("SCHEDLR received workOrder " + workOrderEvent.woid);
                    log.warn("SCHEDLR analysing asked workOrder");
                    log.warn("SCHEDLR accepting workOrder");

                    try {
                        iexecHubForScheduler.acceptWorkOrder(workOrderEvent.woid, workOrderEvent.workerPool).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
    }

    private void watchWorkOrderAcceptedAndCallForContribution() {
        log.info("watchWorkOrderAcceptedAndCallForContribution" + workerPoolForScheduler);

        workerPoolForScheduler.workOrderAcceptedEventObservable(START, END)
                .subscribe(workOrderAcceptedEvent -> {
                    log.warn("SCHEDLR received workOrderAcceptedEvent" + workOrderAcceptedEvent.woid);
                    log.warn("SCHEDLR choosing a random worker");
                    log.warn("SCHEDLR calling pool for contribution of worker1 " + worker);
                    try {
                        log.info(workerPoolForScheduler.callForContribution(workOrderAcceptedEvent.woid, worker, "0").send().getGasUsed().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void watchCallForContributionAndContribute() {
        log.info("watchCallForContributionAndContribute");
        workerPoolForWorker.callForContributionEventObservable(START, END)
                .subscribe(callForContributionEvent -> {
                    log.warn("WORKER1 received callForContributionEvent for worker " + callForContributionEvent.worker);
                    log.warn("WORKER1 executing work");
                    log.warn("WORKER1 contributing");

                    String hashResult = hashResult(WORKER_RESULT);
                    String signResult = signByteResult(WORKER_RESULT, worker);

                    log.info("WORKER1 found hashResult " + hashResult);
                    log.info("WORKER1 found signResult " + signResult);

                    byte[] hashResultBytes = Numeric.hexStringToByteArray(hashResult);
                    byte[] hashSignBytes = Numeric.hexStringToByteArray(signResult);
                    byte[] r = Numeric.hexStringToByteArray(asciiToHex("0"));

                    try {
                        workerPoolForWorker.contribute(callForContributionEvent.woid, hashResultBytes, hashSignBytes, BigInteger.ZERO, r, r).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void watchContributeAndRevealConsensus() {
        log.info("watchContributeAndRevealConsensus");
        workerPoolForScheduler.contributeEventObservable(START, END)
                .subscribe(contributeEvent -> {
                    log.warn("SCHEDLR received contributeEvent " + contributeEvent.woid);
                    log.warn("SCHEDLR checking if consensus reached?");
                    log.warn("SCHEDLR found consensus reached");
                    log.warn("SCHEDLR reavealing consensus");
                    byte[] consensus = Numeric.hexStringToByteArray(hashResult(WORKER_RESULT));
                    try {
                        workerPoolForScheduler.revealConsensus(contributeEvent.woid, consensus).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void watchRevealConsensusAndReveal() {
        log.info("watchRevealConsensusAndReveal");
        workerPoolForWorker.revealConsensusEventObservable(START, END)
                .subscribe(revealConsensusEvent -> {
                    log.warn("WORKER1 received revealConsensusEvent " + revealConsensusEvent.woid);
                    log.warn("WORKER1 reavealing WORKER_RESULT");
                    byte[] result = Numeric.hexStringToByteArray(Hash.sha3String(WORKER_RESULT));
                    workerPoolForWorker.reveal(revealConsensusEvent.woid, result);
                });
    }

    private void watchRevealAndFinalizeWork() {
        log.info("watchRevealAndFinalizeWork");
        workerPoolForScheduler.revealEventObservable(START, END)
                .subscribe(revealEvent -> {
                    log.warn("SCHEDLR received revealEvent ");
                    log.warn("SCHEDLR checking if reveal timeout reached?");
                    log.warn("SCHEDLR found reveal timeout reached");
                    log.warn("SCHEDLR finalazing task");
                    workerPoolForScheduler.finalizedWork(revealEvent.woid, "aStdout", "aStderr", "anUri");
                });
    }

    public boolean createWorkOrder(String workerPool, String app, String dataset, String workOrderParam, BigInteger workReward, BigInteger askedTrust, Boolean dappCallback, String beneficiary) throws Exception {
        boolean gasOk = false;

        if (isWorkerSubscribed()){
            TransactionReceipt tr = getIexecHubForScheduler().createWorkOrder(workerPool, app, dataset, workOrderParam, workReward, askedTrust, dappCallback, beneficiary).send();
            gasOk = !tr.getGasUsed().equals(Contract.GAS_LIMIT);
        }
        return gasOk;
    }


    public String getWorkerPoolAddress() {
        return workerPoolAddress;
    }

    public IexecHub getIexecHubForScheduler() {
        return iexecHubForScheduler;
    }

    public IexecHub getIexecHubForWorker() {
        return iexecHubForWorker;
    }

    public boolean isWorkerSubscribed() {
        return workerSubscribed;
    }

}
