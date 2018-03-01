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
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;

import static com.iexec.scheduler.Utils.hashResult;

@Service
public class MockWatcherService {

    private static final Logger log = LoggerFactory.getLogger(MockWatcherService.class);
    private static final DefaultBlockParameterName END = DefaultBlockParameterName.LATEST;
    private Web3j web3j;
    private Credentials schedulerCredentials;
    private IexecHub iexecHub;
    private String workerPoolAddress;
    private WorkerPool workerPoolForScheduler;
    private boolean workerSubscribed;

    @Value("${ethereum.start-block}")
    private BigInteger startBlock;
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


    //TODO
    //@Autowired
    //private MockProperties mockProperties;

    //Mock values
    @Value("${mock.createWorkerPool.name}")
    private String workerPoolName;
    @Value("${mock.createWorkerPool.subscriptionLockStakePolicy}")
    private BigInteger subscriptionLockStakePolicy;
    @Value("${mock.createWorkerPool.subscriptionLockStakePolicy}")
    private BigInteger subscriptionMinimumStakePolicy;
    @Value("${mock.createWorkerPool.subscriptionLockStakePolicy}")
    private BigInteger subscriptionMinimumScorePolicy;
    @Value("${mock.callForContribution.worker}")
    private String callForContributionWorker;
    @Value("${mock.callForContribution.enclaveChallenge}")
    private String callForContributionEnclaveChallenge;
    @Value("${mock.worker-result}")
    private String workerResult;
    @Value("${mock.finalizeWork.stdout}")
    private String finalizeWorkStdout;
    @Value("${mock.finalizeWork.stderr}")
    private String finalizeWorkStderr;
    @Value("${mock.finalizeWork.uri}")
    private String finalizeWorkUri;

    @Autowired
    public MockWatcherService(Web3j web3j) {
        this.web3j = web3j;
    }

    @Autowired
    private WorkerPoolPolicyConfig workerPoolPolicyConfig;

    @PostConstruct
    public void run() throws Exception {
        init();
        createWorkerPool();
        changeWorkerPoolPolicy();
        watchSubscriptionAndSetWorkerSubscribed();
        watchWorkOrderAndAcceptWorkOrder();
        watchWorkOrderAcceptedAndCallForContribution();
        watchContributeAndRevealConsensus();
        watchRevealAndFinalizeWork();
    }

    private void init() throws IOException, CipherException {
        log.info("SCHEDLR connected to Ethereum client version: " + web3j.web3ClientVersion().send().getWeb3ClientVersion());
        log.info("SCHEDLR loading credentials and contracts");
        schedulerCredentials = WalletUtils.loadCredentials(schedulerWalletPassword, walletFolder + "/" + schedulerWalletFilename);
        iexecHub = IexecHub.load(
                iexecHubAddress, web3j, schedulerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    private void createWorkerPool() throws Exception {
        log.info("SCHEDLR creating WorkerPool: " + workerPoolName);
        iexecHub.createWorkerPool(workerPoolName, subscriptionLockStakePolicy, subscriptionMinimumStakePolicy, subscriptionMinimumScorePolicy).send();
    }

    private void changeWorkerPoolPolicy() {
        log.info("SCHEDLR watching CreateWorkerPoolEvent");
        iexecHub.createWorkerPoolEventObservable(getStartBlock(), END)
                .subscribe(createWorkerPoolEvent -> {
                    if (createWorkerPoolEvent.workerPoolName.equals(workerPoolName)) {
                        workerPoolAddress = createWorkerPoolEvent.workerPool;
                        log.warn("SCHEDLR received CreateWorkerPoolEvent " + createWorkerPoolEvent.workerPoolName + ":" + workerPoolAddress);
                        workerPoolForScheduler = WorkerPool.load(
                                workerPoolAddress, web3j, schedulerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                        try {
                            String m_workersAuthorizedListAddress = workerPoolForScheduler.m_workersAuthorizedListAddress().send();

                            AuthorizedList workerAuthorizedList = AuthorizedList.load(
                                    m_workersAuthorizedListAddress, web3j, schedulerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                            log.info("SCHEDLR updating Policy " + workerPoolPolicyConfig.getMode() + " " + workerPoolPolicyConfig.getList().toString());
                            workerAuthorizedList.changeListPolicy(workerPoolPolicyConfig.getMode()).send();
                            workerAuthorizedList.updateBlacklist(workerPoolPolicyConfig.getList(), true).send();
                            watchPolicyChange(workerAuthorizedList);
                            watchBlacklistChange(workerAuthorizedList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void watchPolicyChange(AuthorizedList authorizedList) {
        log.info("SCHEDLR watching PolicyChangeEvent");
        authorizedList.policyChangeEventObservable(getStartBlock(), END)
                .subscribe(policyChangeEvent -> {
                    log.info("SCHEDLR received policyChangeEvent on workerpool from " + policyChangeEvent.oldPolicy + " to " + policyChangeEvent.newPolicy);
                });
    }

    private void watchBlacklistChange(AuthorizedList authorizedList) {
        log.info("SCHEDLR watching BlacklistChangeEvent");
        authorizedList.blacklistChangeEventObservable(getStartBlock(), END)
                .subscribe(blacklistChangeEvent -> {
                    log.info("SCHEDLR received BlacklistChangeEvent: " + blacklistChangeEvent.actor + " listed " + blacklistChangeEvent.isBlacklisted);
                });
    }

    private void watchSubscriptionAndSetWorkerSubscribed() {
        log.info("SCHEDLR watching WorkerPoolSubscriptionEvent");
        iexecHub.workerPoolSubscriptionEventObservable(getStartBlock(), END)
                .subscribe(workerPoolSubscriptionEvent -> {
                    if (workerPoolSubscriptionEvent.workerPool.equals(workerPoolAddress)) {
                        log.warn("SCHEDLR received WorkerPoolSubscriptionEvent for worker " + workerPoolSubscriptionEvent.worker);
                        workerSubscribed = true;
                        //now clouduser able to createWorkOrder
                    }
                });
    }

    private void watchWorkOrderAndAcceptWorkOrder() {
        log.info("SCHEDLR watching WorkOrderEvent (auto accept)");
        iexecHub.workOrderEventObservable(getStartBlock(), END)
                .subscribe(workOrderEvent -> {
                    log.warn("SCHEDLR received WorkOrderEvent " + workOrderEvent.woid);
                    log.warn("SCHEDLR analysing asked workOrder");
                    log.warn("SCHEDLR accepting workOrder");

                    try {
                        iexecHub.acceptWorkOrder(workOrderEvent.woid, workOrderEvent.workerPool).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
    }

    private void watchWorkOrderAcceptedAndCallForContribution() {
        log.info("SCHEDLR watching WorkOrderAcceptedEvent (auto callForContribution)");
        workerPoolForScheduler.workOrderAcceptedEventObservable(getStartBlock(), END)
                .subscribe(workOrderAcceptedEvent -> {
                    log.warn("SCHEDLR received WorkOrderAcceptedEvent" + workOrderAcceptedEvent.woid);
                    log.warn("SCHEDLR choosing a random worker");
                    log.warn("SCHEDLR calling pool for contribution of worker1 " + callForContributionWorker);
                    try {
                        log.info(workerPoolForScheduler.callForContribution(workOrderAcceptedEvent.woid, callForContributionWorker, callForContributionEnclaveChallenge).send().getGasUsed().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void watchContributeAndRevealConsensus() {
        log.info("SCHEDLR watching ContributeEvent (auto revealConsensus)");
        workerPoolForScheduler.contributeEventObservable(getStartBlock(), END)
                .subscribe(contributeEvent -> {
                    log.warn("SCHEDLR received ContributeEvent " + contributeEvent.woid);
                    log.warn("SCHEDLR checking if consensus reached?");
                    log.warn("SCHEDLR found consensus reached");
                    log.warn("SCHEDLR reavealing consensus " + hashResult(workerResult));
                    byte[] consensus = Numeric.hexStringToByteArray(hashResult(workerResult));
                    try {
                        workerPoolForScheduler.revealConsensus(contributeEvent.woid, consensus).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void watchRevealAndFinalizeWork() {
        log.info("SCHEDLR watching RevealEvent (auto finalizeWork)");
        workerPoolForScheduler.revealEventObservable(getStartBlock(), END)
                .subscribe(revealEvent -> {
                    log.warn("SCHEDLR received RevealEvent ");
                    log.warn("SCHEDLR checking if reveal timeout reached?");
                    log.warn("SCHEDLR found reveal timeout reached");
                    log.warn("SCHEDLR finalazing task");
                    workerPoolForScheduler.finalizedWork(revealEvent.woid, finalizeWorkStdout, finalizeWorkStderr, finalizeWorkUri);//"aStdout", "aStderr", "anUri"
                });
    }

    public boolean createWorkOrder(String workerPool, String app, String dataset, String workOrderParam, BigInteger workReward, BigInteger askedTrust, Boolean dappCallback, String beneficiary) throws Exception {
        boolean gasOk = false;

        if (isWorkerSubscribed()) {
            TransactionReceipt tr = getIexecHub().createWorkOrder(workerPool, app, dataset, workOrderParam, workReward, askedTrust, dappCallback, beneficiary).send();
            gasOk = !tr.getGasUsed().equals(Contract.GAS_LIMIT);
        }
        return gasOk;
    }

    public String getWorkerPoolAddress() {
        return workerPoolAddress;
    }

    public IexecHub getIexecHub() {
        return iexecHub;
    }

    public boolean isWorkerSubscribed() {
        return workerSubscribed;
    }

    private DefaultBlockParameter getStartBlock() {
        return DefaultBlockParameter.valueOf(startBlock);
    }
}
