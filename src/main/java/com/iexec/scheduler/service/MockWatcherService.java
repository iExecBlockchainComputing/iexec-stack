package com.iexec.scheduler.service;

import com.iexec.scheduler.contracts.generated.AuthorizedList;
import com.iexec.scheduler.contracts.generated.IexecHub;
import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.helper.MockConfig;
import com.iexec.scheduler.helper.WorkerPoolConfig;
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

import static com.iexec.scheduler.helper.Utils.hashResult;

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

    @Value("${ethereum.startBlock}")
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
    @Autowired
    private WorkerPoolConfig poolConfig;
    @Autowired
    private MockConfig mockConfig;

    @Autowired
    public MockWatcherService(Web3j web3j) {
        this.web3j = web3j;
    }

    @PostConstruct
    public void run() throws Exception {
        init();
        createWorkerPool();
        loadAndSetupWorkerPool();
    }

    private void onWorkerPoolSetupCompleted() {
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
        log.info("SCHEDLR creating WorkerPool: " + poolConfig.getName());
        log.info(
                iexecHub.createWorkerPool(poolConfig.getName(),
                        poolConfig.getSubscriptionLockStakePolicy(),
                        poolConfig.getSubscriptionMinimumStakePolicy(),
                        poolConfig.getSubscriptionMinimumScorePolicy()).send().getGasUsed().toString()
        );
    }

    private void loadAndSetupWorkerPool() {
        log.info("SCHEDLR watching CreateWorkerPoolEvent");
        iexecHub.createWorkerPoolEventObservable(getStartBlock(), END)
                .subscribe(createWorkerPoolEvent -> {
                    log.info(createWorkerPoolEvent.workerPoolName);
                    if (createWorkerPoolEvent.workerPoolName.equals(poolConfig.getName())) {
                        workerPoolAddress = createWorkerPoolEvent.workerPool;
                        log.warn("SCHEDLR received CreateWorkerPoolEvent " + createWorkerPoolEvent.workerPoolName + ":" + workerPoolAddress);
                        workerPoolForScheduler = WorkerPool.load(
                                workerPoolAddress, web3j, schedulerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                        setupWorkerPool();
                    }
                });
    }

    private void setupWorkerPool() {
        try {
            String workersAuthorizedListAddress = workerPoolForScheduler.m_workersAuthorizedListAddress().send();
            AuthorizedList workerAuthorizedList = AuthorizedList.load(
                    workersAuthorizedListAddress, web3j, schedulerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

            watchWorkerPoolPolicy();
            watchPolicyChange(workerAuthorizedList);
            watchBlacklistChange(workerAuthorizedList);

            updateWorkerPoolPolicy();
            workerAuthorizedList.changeListPolicy(poolConfig.getMode()).send();
            workerAuthorizedList.updateBlacklist(poolConfig.getList(), true).send();

            onWorkerPoolSetupCompleted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateWorkerPoolPolicy() throws Exception {
        log.info("SCHEDLR pool policy needs changes?");
        if (!poolConfig.getStakeRatioPolicy().equals(workerPoolForScheduler.m_stakeRatioPolicy().send()) ||
                !poolConfig.getSchedulerRewardRatioPolicy().equals(workerPoolForScheduler.m_schedulerRewardRatioPolicy().send()) ||
                !poolConfig.getSubscriptionMinimumStakePolicy().equals(workerPoolForScheduler.m_subscriptionMinimumStakePolicy().send()) ||
                !poolConfig.getSubscriptionMinimumScorePolicy().equals(workerPoolForScheduler.m_subscriptionMinimumScorePolicy().send())) {

            workerPoolForScheduler.changeWorkerPoolPolicy(poolConfig.getStakeRatioPolicy(),
                    poolConfig.getSchedulerRewardRatioPolicy(),
                    poolConfig.getResultRetentionPolicy(),
                    poolConfig.getSubscriptionMinimumStakePolicy(),
                    poolConfig.getSubscriptionMinimumScorePolicy()).send();
            log.info("SCHEDLR yes");
        } else {
            log.info("SCHEDLR no");
        }
    }

    private void watchWorkerPoolPolicy() {
        log.info("SCHEDLR watching WorkerPoolPolicyUpdateEvent");
        workerPoolForScheduler.workerPoolPolicyUpdateEventObservable(getStartBlock(), END)
                .subscribe(workerPoolPolicyUpdateEvent -> {
                    log.info("SCHEDLR received WorkerPoolPolicyUpdateEvent (newSchedulerRewardRatioPolicy,..)");
                });
    }

    private void watchPolicyChange(AuthorizedList authorizedList) {
        log.info("SCHEDLR watching PolicyChangeEvent (black/whitelist)");
        authorizedList.policyChangeEventObservable(getStartBlock(), END)
                .subscribe(policyChangeEvent -> {
                    log.info("SCHEDLR received policyChangeEvent (black/whitelist) on workerpool from " + policyChangeEvent.oldPolicy + " to " + policyChangeEvent.newPolicy);
                });
    }

    private void watchBlacklistChange(AuthorizedList authorizedList) {
        log.info("SCHEDLR watching BlacklistChangeEvent (0xbadworker,..)");
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
                    log.warn("SCHEDLR calling pool for contribution of worker1 " + mockConfig.getCallForContribution().getWorker());
                    try {
                        log.info(workerPoolForScheduler.callForContribution(workOrderAcceptedEvent.woid,
                                mockConfig.getCallForContribution().getWorker(),
                                mockConfig.getCallForContribution().getEnclaveChallenge()).send().getGasUsed().toString());
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
                    log.warn("SCHEDLR reavealing consensus " + hashResult(mockConfig.getWorkerResult()));
                    byte[] consensus = Numeric.hexStringToByteArray(hashResult(mockConfig.getWorkerResult()));
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
                    workerPoolForScheduler.finalizedWork(revealEvent.woid,
                            mockConfig.getFinalizeWork().getStdout(),
                            mockConfig.getFinalizeWork().getStderr(),
                            mockConfig.getFinalizeWork().getUri());//"aStdout", "aStderr", "anUri"
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
