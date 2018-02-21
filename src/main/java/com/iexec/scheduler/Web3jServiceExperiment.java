package com.iexec.scheduler;

import com.iexec.scheduler.contracts.generated.AuthorizedList;
import com.iexec.scheduler.contracts.generated.IexecHub;
import com.iexec.scheduler.contracts.generated.WorkerPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;

@Component
public class Web3jServiceExperiment {

    private static final Logger log = LoggerFactory.getLogger(Web3jServiceExperiment.class);

    private static final DefaultBlockParameterName START    = DefaultBlockParameterName.EARLIEST;
    private static final DefaultBlockParameterName END      = DefaultBlockParameterName.LATEST;
    private static final BigInteger BLACKLIST       = BigInteger.ONE;
    private static final String WALLET_PATH         = "./src/main/resources/wallet";
    private static final String SCHEDULER_ADDRESS   = "0x8bd535d49b095ef648cd85ea827867d358872809";
    private static final String WORKER_ADDRESS      = "0x70a1bebd73aef241154ea353d6c8c52d420d4f5b";
    private static final String SCHEDULER_WALLET    = WALLET_PATH + "/UTC--2018-02-14T08-32-12.500000000Z--8bd535d49b095ef648cd85ea827867d358872809.json";
    private static final String WORKER_WALLET       = WALLET_PATH + "/UTC--2018-02-14T11-15-48.411000000Z--70a1bebd73aef241154ea353d6c8c52d420d4f5b.json";
    private static final String WORKER_RESULT       = "iExec the wanderer";
    private static final String WALLET_PASSWORD     = "whatever";

    private Web3j web3j;
    private Credentials schedulerCredentials;
    private Credentials workerCredentials;
    private IexecHub iexecHubForScheduler;
    private IexecHub iexecHubForWorker;
    private String workerPoolAddress;
    private WorkerPool workerPoolForScheduler;
    private WorkerPool workerPoolForWorker;

    @Autowired
    public Web3jServiceExperiment(Web3j web3j) {
        this.web3j = web3j;
    }

    @Value("${ethereum.address.iexecHub}")
    private String iexecHubAddress;

    @Value("${ethereum.address.appAdress}")
    private String appAddress;

    @Value("${ethereum.address.iExecCloudUser}")
    private String iExecCloudUser;

    @PostConstruct
    public void run() throws Exception {
        init();

        createWorkerPool();
        watchCreateWorkerPoolAndSubscribe();
        watchSubscriptionAndCreateTaskRequest();
        watchCreateTaskRequest();
        watchTaskReceivedAndAcceptTask();
        watchTaskAcceptedAndCallForContribution();
        watchCallForContributionAndContribute();
        watchContributeAndRevealConsensus();

        //watchRevealConsensus();
        //watchReveal();
    }

    private void init() throws IOException, CipherException {
        log.info("Connected to Ethereum client version: " + web3j.web3ClientVersion().send().getWeb3ClientVersion());

        log.info("Loading credentials");
        schedulerCredentials = WalletUtils.loadCredentials(WALLET_PASSWORD, SCHEDULER_WALLET);
        workerCredentials = WalletUtils.loadCredentials("whatever", WORKER_WALLET);

        log.info("Loading smart contracts");
        iexecHubForScheduler = IexecHub.load(
                iexecHubAddress, web3j, schedulerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        iexecHubForWorker = IexecHub.load(
                iexecHubAddress, web3j, workerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    private void createWorkerPool() throws Exception {
        String workerPoolName = "myWorkerPool-" + System.currentTimeMillis();
        log.info("SCHEDLR creating workerPool: " + workerPoolName);
        iexecHubForScheduler.createWorkerPool(workerPoolName, BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO).send();
    }

    private void watchCreateWorkerPoolAndSubscribe() {
        iexecHubForScheduler.createWorkerPoolEventObservable(START, END)
                .subscribe(createWorkerPoolEvent ->{
                            //if (createWorkerPoolEvent.name.equals(workerPoolName)){
                    workerPoolAddress = createWorkerPoolEvent.workerPool;
                    log.warn("SCHEDLR received createWorkerPoolEvent " + createWorkerPoolEvent.name + ":" + workerPoolAddress);
                            workerPoolForScheduler = WorkerPool.load(
                                    workerPoolAddress, web3j, schedulerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                            workerPoolForWorker = WorkerPool.load(
                                    workerPoolAddress, web3j, workerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

                            try {
                                String m_workersAuthorizedListAddress = workerPoolForScheduler.m_workersAuthorizedListAddress().send();

                                AuthorizedList authorizedList = AuthorizedList.load(
                                            m_workersAuthorizedListAddress, web3j, schedulerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

                                authorizedList.changeListPolicy(BLACKLIST).send();
                                watchPolicyChangeAndSubscribe(authorizedList);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //}
                        });
    }

    private void watchPolicyChangeAndSubscribe(AuthorizedList authorizedList) {
        authorizedList.policyChangeEventObservable(START, END)
                .subscribe(policyChangeEvent ->{
                    log.info("SCHEDLR received policyChangeEvent on workerpool from "+ policyChangeEvent.oldPolicy + " to " + policyChangeEvent.newPolicy);

                    try {
                        log.info("WORKER1 subscribing to workerPool");
                        log.info(workerPoolForScheduler.subscribeToPool().send().getGasUsed().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
    }

    private void watchSubscriptionAndCreateTaskRequest() {
        iexecHubForWorker.workerPoolSubscriptionEventObservable(START, END)
                .subscribe(workerPoolSubscriptionEvent ->{
                    //if (workerPoolSubscriptionEvent.workerPool.equals(workerPoolAddress)){
                    log.warn("WORKER1 received workerPoolSubscriptionEvent " + workerPoolSubscriptionEvent.worker);
                    log.info("CLDUSER creating taskRequest");
                    try {
                        iexecHubForScheduler.createTaskRequest(workerPoolAddress, appAddress, "0x0000000000000000000000000000000000000000", "noTaskParam", BigInteger.ZERO, BigInteger.ONE, false, iExecCloudUser).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //
                });
    }

    private void watchCreateTaskRequest() {
        iexecHubForScheduler.taskRequestEventObservable(START, END)
                .subscribe(taskRequestEvent ->{
                    //if (taskRequestEvent.workerPool.equals(workerPoolAddress)){
                    log.warn("CLDUSER received taskRequestEvent:  taskId " + taskRequestEvent.taskID);
                    log.warn("                                    workerPool " +taskRequestEvent.workerPool);
                    log.warn("                                    app " +taskRequestEvent.app);
                    //}
                });
    }

    private void watchTaskReceivedAndAcceptTask() {
        workerPoolForScheduler.taskReceivedEventObservable(START, END)
                .subscribe(taskReceivedEvent ->{
                    //if (taskReceivedEvent.taskID.equals(taskRequestEvent.taskID)){
                    log.warn("SCHEDLR received taskReceivedEvent " + taskReceivedEvent.taskID);
                    log.warn("SCHEDLR Analysing asked task");
                    log.warn("SCHEDLR Accepting task");
                    try {
                        workerPoolForScheduler.acceptTask(taskReceivedEvent.taskID).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //}

                });
    }

    private void watchTaskAcceptedAndCallForContribution() {
        workerPoolForScheduler.taskAcceptedEventObservable(START, END)
                .subscribe(taskAcceptedEvent ->{
                    //if (taskAcceptedEvent.taskID.equals(taskRequestEvent.taskID)){
                    log.warn("SCHEDLR received taskAcceptedEvent"  + taskAcceptedEvent.taskID);
                    log.warn("SCHEDLR choosing a random worker");
                    log.warn("SCHEDLR calling pool for contribution of worker1 " + WORKER_ADDRESS);
                    try {
                       log.info(workerPoolForScheduler.callForContribution(taskAcceptedEvent.taskID, WORKER_ADDRESS, "0x0000000000000000000000000000000000000000").send().getGasUsed().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //}
                });
    }

    private void watchCallForContributionAndContribute() {
        workerPoolForWorker.callForContributionEventObservable(START, END)
                .subscribe(callForContributionEvent ->{
                    log.warn("WORKER1 received callForContributionEvent for worker "+ callForContributionEvent.worker);
                    //if (callForContributionEvent.worker.equals(WORKER_ADDRESS)){
                    log.warn("WORKER1 executing work");
                    log.warn("WORKER1 contributing");

                    log.info("WORKER1 found hashByteResult " + Hash.sha3String(Hash.sha3(WORKER_RESULT)));
                    log.info("WORKER1 found signByteResult " + signByteResult(Hash.sha3String(WORKER_RESULT), WORKER_ADDRESS));

                    try {
                        workerPoolForWorker.contribute(callForContributionEvent.taskID,  Hash.sha3String(Hash.sha3(WORKER_RESULT)).getBytes(), signByteResult(Hash.sha3String(WORKER_RESULT), WORKER_ADDRESS).getBytes(), BigInteger.ZERO, "0".getBytes(), "0".getBytes()).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //}
                });
    }

    private void watchContributeAndRevealConsensus() {
        workerPoolForScheduler.contributeEventObservable(START, END)
                .subscribe(contributeEvent ->{
                    log.warn("SCHEDLR received contributeEvent " + contributeEvent.taskID);
                    log.warn("SCHEDLR checking if consensus reached?");
                    log.warn("SCHEDLR found consensus reached");
                    log.warn("SCHEDLR reavealing consensus");
                    try {
                        workerPoolForScheduler.revealConsensus(contributeEvent.taskID,  "consensus".getBytes()).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void watchReveal() {
        workerPoolForScheduler.revealEventObservable(START, END)
                .subscribe(revealEvent ->{
                    log.warn("SCHEDLR received revealEvent ");
                    log.warn("SCHEDLR checking if reveal timeout reached?");
                    log.warn("SCHEDLR found reveal timeout reached");
                    log.warn("SCHEDLR finalazing task");
                    workerPoolForWorker.finalizedTask(revealEvent.taskID,  "stdout", "stderr","http://uri");
                });
    }

    private void watchRevealConsensus() {
        workerPoolForWorker.revealConsensusEventObservable(START, END)
                .subscribe(revealConsensusEvent ->{
                    log.warn("WORKER1 received revealConsensusEvent"+ revealConsensusEvent.taskID);
                    log.warn("WORKER1 reavealing WORKER_RESULT");
                    workerPoolForWorker.reveal(revealConsensusEvent.taskID,  "WORKER_RESULT".getBytes());
                });
    }


    private String signByteResult(String resultHash, String address) {
        String addressHash = Hash.sha3(address);
        String xor = "0x";
        log.info(resultHash);
        log.info(addressHash);
        for (int i = 2; i <66 ; i++) {
            Integer temp = Integer.parseInt(String.valueOf(resultHash.charAt(i)), 16) ^ Integer.parseInt(String.valueOf(addressHash.charAt(i)), 16);
            xor+= ""+Integer.toHexString(temp);
        }
        String sign = Hash.sha3(xor.toString());
        log.info("xor "+xor);
        log.info("sign "+sign);

        return sign;
    }

    private String web3Sha3(String preimage) throws IOException {
        return  web3j.web3Sha3(preimage).send().getResult();
    }

}
