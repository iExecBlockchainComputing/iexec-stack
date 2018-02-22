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
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;

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

    public static String asciiToHex(String asciiValue)
    {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++)
        {
            hex.append(Integer.toHexString((int) chars[i]));
        }

        return hex.toString() + "".join("", Collections.nCopies(32 - (hex.length()/2), "00"));
    }

    @PostConstruct
    public void run() throws Exception {
        
        init();
        createWorkerPool();
        watchCreateWorkerPoolAndSubscribe();
        watchSubscriptionAndCreateWorkOrder();
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
        schedulerCredentials = WalletUtils.loadCredentials(WALLET_PASSWORD, SCHEDULER_WALLET);
        workerCredentials = WalletUtils.loadCredentials(WALLET_PASSWORD, WORKER_WALLET);

        log.info("Loading smart contracts");
        iexecHubForScheduler = IexecHub.load(
                iexecHubAddress, web3j, schedulerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        iexecHubForWorker = IexecHub.load(
                iexecHubAddress, web3j, workerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    private void createWorkerPool() throws Exception {
        log.info("createWorkerPool");
        String workerPoolName = "myWorkerPool-" + System.currentTimeMillis();
        log.info("SCHEDLR creating workerPool: " + workerPoolName);
        iexecHubForScheduler.createWorkerPool(workerPoolName, BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO).send();
    }

    private void watchCreateWorkerPoolAndSubscribe() {
        log.info("watchCreateWorkerPoolAndSubscribe");
        iexecHubForScheduler.createWorkerPoolEventObservable(START, END)
                .subscribe(createWorkerPoolEvent ->{
                            //if (createWorkerPoolEvent.name.equals(workerPoolName)){
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
                                watchPolicyChangeAndSubscribe(authorizedList);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //}
                        });
    }

    private void watchPolicyChangeAndSubscribe(AuthorizedList authorizedList) {
        log.info("watchPolicyChangeAndSubscribe");
        authorizedList.policyChangeEventObservable(START, END)
                .subscribe(policyChangeEvent ->{
                    log.info("SCHEDLR received policyChangeEvent on workerpool from "+ policyChangeEvent.oldPolicy + " to " + policyChangeEvent.newPolicy);

                    try {
                        log.info("WORKER1 subscribing to workerPool");
                        log.info(workerPoolForWorker.subscribeToPool().send().getGasUsed().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
    }

    private void watchSubscriptionAndCreateWorkOrder() {
        log.info("watchSubscriptionAndCreateWorkOrder");
        iexecHubForWorker.workerPoolSubscriptionEventObservable(START, END)
                .subscribe(workerPoolSubscriptionEvent ->{
                    //if (workerPoolSubscriptionEvent.workerPool.equals(workerPoolAddress)){
                    log.warn("WORKER1 received workerPoolSubscriptionEvent " + workerPoolSubscriptionEvent.worker);
                    log.info("CLDUSER creating workOrder");
                    try {
                        iexecHubForScheduler.createWorkOrder(workerPoolAddress, appAddress, "0", "noTaskParam", BigInteger.ZERO, BigInteger.ONE, false, iExecCloudUser).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //
                });
    }

    private void watchWorkOrderAndAcceptWorkOrder() {
        log.info("watchWorkOrderAndAcceptWorkOrder");
        iexecHubForScheduler.workOrderEventObservable(START, END)
                .subscribe(workOrderEvent ->{
                    //if (taskReceivedEvent.taskID.equals(taskRequestEvent.taskID)){
                    log.warn("SCHEDLR received workOrder " + workOrderEvent.woid);
                    log.warn("SCHEDLR analysing asked workOrder");
                    log.warn("SCHEDLR accepting workOrder");
                    try {
                        iexecHubForScheduler.acceptWorkOrder(workOrderEvent.woid, workerPoolAddress).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //}

                });
    }

    private void watchWorkOrderAcceptedAndCallForContribution() {
        log.info("watchWorkOrderAcceptedAndCallForContribution" +workerPoolForScheduler);

        workerPoolForScheduler.workOrderAcceptedEventObservable(START, END)
                .subscribe(workOrderAcceptedEvent ->{
                    //if (taskAcceptedEvent.taskID.equals(taskRequestEvent.taskID)){
                    log.warn("SCHEDLR received workOrderAcceptedEvent"  + workOrderAcceptedEvent.woid);
                    log.warn("SCHEDLR choosing a random worker");
                    log.warn("SCHEDLR calling pool for contribution of worker1 " + WORKER_ADDRESS);
                    try {
                        log.info(workerPoolForScheduler.callForContribution(workOrderAcceptedEvent.woid, WORKER_ADDRESS, "0").send().getGasUsed().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //}
                });
    }

    private void watchCallForContributionAndContribute() {
        log.info("watchCallForContributionAndContribute");
        workerPoolForWorker.callForContributionEventObservable(START, END)
                .subscribe(callForContributionEvent ->{
                    log.warn("WORKER1 received callForContributionEvent for worker "+ callForContributionEvent.worker);
                    //if (callForContributionEvent.worker.equals(WORKER_ADDRESS)){
                    log.warn("WORKER1 executing work");
                    log.warn("WORKER1 contributing");

                    String hashResult = hashResult(WORKER_RESULT);
                    String signResult = signByteResult(WORKER_RESULT, WORKER_ADDRESS);

                    log.info("WORKER1 found hashResult " + hashResult);
                    log.info("WORKER1 found signResult " + signResult);

                    byte[] hashResultBytes = Numeric.hexStringToByteArray(hashResult);
                    byte[] hashSignBytes = Numeric.hexStringToByteArray(signResult);
                    byte[] r = Numeric.hexStringToByteArray(asciiToHex("0"));

                    try {
                        workerPoolForWorker.contribute(callForContributionEvent.woid, hashResultBytes , hashSignBytes, BigInteger.ZERO, r, r).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //}
                });
    }

    private void watchContributeAndRevealConsensus() {
        log.info("watchContributeAndRevealConsensus");
        workerPoolForScheduler.contributeEventObservable(START, END)
                .subscribe(contributeEvent ->{
                    log.warn("SCHEDLR received contributeEvent " + contributeEvent.woid);
                    log.warn("SCHEDLR checking if consensus reached?");
                    log.warn("SCHEDLR found consensus reached");
                    log.warn("SCHEDLR reavealing consensus");
                    byte[] consensus = Numeric.hexStringToByteArray(hashResult(WORKER_RESULT));
                    try {
                        workerPoolForScheduler.revealConsensus(contributeEvent.woid,  consensus).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void watchRevealConsensusAndReveal() {
        log.info("watchRevealConsensusAndReveal");
        workerPoolForWorker.revealConsensusEventObservable(START, END)
                .subscribe(revealConsensusEvent ->{
                    log.warn("WORKER1 received revealConsensusEvent "+ revealConsensusEvent.woid);
                    log.warn("WORKER1 reavealing WORKER_RESULT");
                    byte[] result = Numeric.hexStringToByteArray(Hash.sha3String(WORKER_RESULT));
                    workerPoolForWorker.reveal(revealConsensusEvent.woid,  result);
                });
    }

    private void watchRevealAndFinalizeWork() {
        log.info("watchRevealAndFinalizeWork");
        workerPoolForScheduler.revealEventObservable(START, END)
                .subscribe(revealEvent ->{
                    log.warn("SCHEDLR received revealEvent ");
                    log.warn("SCHEDLR checking if reveal timeout reached?");
                    log.warn("SCHEDLR found reveal timeout reached");
                    log.warn("SCHEDLR finalazing task");
                    workerPoolForScheduler.finalizedWork(revealEvent.woid,  "aStdout", "aStderr","anUri");
                });
    }




    private String signByteResult(String result, String address) {
        String resultHash = Hash.sha3String(result);
        String addressHash = Hash.sha3(address);
        String xor = "0x";
        for (int i = 2; i <66 ; i++) {
            Integer temp = Integer.parseInt(String.valueOf(resultHash.charAt(i)), 16) ^ Integer.parseInt(String.valueOf(addressHash.charAt(i)), 16);
            xor+= Integer.toHexString(temp);
        }
        String sign = Hash.sha3(xor);
        log.info("xor "+xor);
        log.info("sign "+sign);
        return sign;
    }

    private String hashResult(String result) {
        return Hash.sha3(Hash.sha3String(result));
    }

    private String web3Sha3(String preimage) throws IOException {
        return  web3j.web3Sha3(preimage).send().getResult();
    }

}
