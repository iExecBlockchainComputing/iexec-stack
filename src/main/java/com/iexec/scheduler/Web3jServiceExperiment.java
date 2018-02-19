package com.iexec.scheduler;

import com.iexec.scheduler.contracts.generated.IexecHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import rx.Subscription;

import javax.annotation.PostConstruct;
import java.math.BigInteger;

@Component
public class Web3jServiceExperiment {

    private static final Logger log = LoggerFactory.getLogger(Web3jServiceExperiment.class);

    private Web3j web3j;

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

    public void printAdresses() throws Exception {
        System.out.println("iexecHubAddress: " + iexecHubAddress);
        System.out.println("appAddress: " + appAddress);
        System.out.println("iExecCloudUser: " + iExecCloudUser);
    }

    @PostConstruct
    public void run() throws Exception {
        System.out.println(iexecHubAddress);

        System.out.println("I'm started after construction");
        String walletPath = "/home/ugo/iexecdev/iexec-scheduler/src/main/resources/wallet";
        Credentials schedulerCredentials = WalletUtils.loadCredentials(
                "whatever",
                walletPath + "/UTC--2018-02-14T08-32-12.500000000Z--8bd535d49b095ef648cd85ea827867d358872809.json");

        Credentials workerCredentials =
                WalletUtils.loadCredentials(
                        "whatever",
                        walletPath + "/UTC--2018-02-14T11-15-48.411000000Z--70a1bebd73aef241154ea353d6c8c52d420d4f5b.json");
        log.info("Credentials loaded");

        String schedulerAddress = "0x8bd535d49b095ef648cd85ea827867d358872809";
        String workerAddress = "0x70a1bebd73aef241154ea353d6c8c52d420d4f5b";

        log.info("Loading smart contracts");
        System.out.println("Loading smart contracts");
        IexecHub iexecHubForScheduler = IexecHub.load(
                iexecHubAddress, web3j, schedulerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        // IexecHub iexecHubForWorker = IexecHub.load(
        //        iexecHubAddress, web3j, workerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

        System.out.println("before subscription");
        Subscription createWorkerPoolEventSubscription = iexecHubForScheduler.createWorkerPoolEventObservable(
                DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                .subscribe(createWorkerPoolEvent -> {
                            String workerPoolContractAddress = createWorkerPoolEvent.workerPool;
                            log.warn("SCHEDLR received createWorkerPoolEvent " + createWorkerPoolEvent.name + ":" + workerPoolContractAddress);

                        },
                        Throwable::printStackTrace);

        System.out.println("after subscription");
        log.info("SCHEDLR creating workerPool");
        iexecHubForScheduler.createWorkerPool("myWorkerPool", BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO).send();

    }


    /*
    private void run() throws Exception {

        log.info("Connected to Ethereum client version: "
                + web3j.web3ClientVersion().send().getWeb3ClientVersion());

        String walletPath = "/home/james/iexecdev/web3j-sample-project-gradle/src/main/resources/wallet";

        Credentials schedulerCredentials =
                WalletUtils.loadCredentials(
                        "whatever",
                        walletPath+"/UTC--2018-02-14T08-32-12.500000000Z--8bd535d49b095ef648cd85ea827867d358872809.json");
        Credentials workerCredentials =
                WalletUtils.loadCredentials(
                        "whatever",
                        walletPath+"/UTC--2018-02-14T11-15-48.411000000Z--70a1bebd73aef241154ea353d6c8c52d420d4f5b.json");
        log.info("Credentials loaded");

        String schedulerAddress = "0x8bd535d49b095ef648cd85ea827867d358872809";
        String workerAddress = "0x70a1bebd73aef241154ea353d6c8c52d420d4f5b";

        String iexecHubAddress = "0x1274b0dd32da965c33882a4082023170b4112a02";
        String appAddress = "0x42e4ad52c913d34f66bdcd23eef5f82c287398d1";
        String iExecCloudUser = "0x7e5375dead8a2b3a527e8d681142ebd116e94ea9";

        log.info("Loading smart contracts");
        IexecHub iexecHubForScheduler = IexecHub.load(
                iexecHubAddress, web3j, schedulerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        IexecHub iexecHubForWorker = IexecHub.load(
                iexecHubAddress, web3j, workerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

        log.info("SCHEDLR creating workerPool");
        iexecHubForScheduler.createWorkerPool("myWorkerPool", BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO);

        Subscription createWorkerPoolEventSubscription = iexecHubForScheduler.createWorkerPoolEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                .subscribe(createWorkerPoolEvent ->{
                            String workerPoolContractAddress = createWorkerPoolEvent.workerPool;
                            log.warn("SCHEDLR received createWorkerPoolEvent " + createWorkerPoolEvent.name + ":"+workerPoolContractAddress);

                            WorkerPool workerPool = WorkerPool.load(
                                    workerPoolContractAddress, web3j, schedulerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

                            log.info("WORKER1 subscribing to workerPool");
                            iexecHubForWorker.subscribeToPool();

                            Subscription workerPoolSubscriptionEventSubscription = iexecHubForWorker.workerPoolSubscriptionEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                                    .subscribe(workerPoolSubscriptionEvent ->{
                                        log.warn("WORKER1 received workerPoolSubscriptionEvent " + workerPoolSubscriptionEvent.worker);
                                        log.info("CLDUSER creating taskRequest");
                                        iexecHubForScheduler.createTaskRequest(createWorkerPoolEvent.workerPool, appAddress, "0", "noTaskParam", BigInteger.ZERO, BigInteger.ONE, false, iExecCloudUser);
                                    });

                            iexecHubForScheduler.taskRequestEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                                    .subscribe(taskRequestEvent ->{
                                        log.warn("CLDUSER received taskRequestEvent:  taskId " + taskRequestEvent.taskID);
                                        log.warn("                                    workerPool " +taskRequestEvent.workerPool);
                                        log.warn("                                    app " +taskRequestEvent.app);
                                    });

                            Subscription taskReceivedEventSubscription = workerPool.taskReceivedEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                                    .subscribe(taskRequestEvent ->{
                                        log.warn("SCHEDLR received taskReceivedEvent " + taskRequestEvent.taskID);
                                        log.warn("SCHEDLR Analysing asked task");
                                        log.warn("SCHEDLR Accepting task");
                                        RemoteCall<TransactionReceipt> rc = workerPool.acceptTask(taskRequestEvent.taskID);
                                        try {
                                            TransactionReceipt transactionReceipt = rc.send();
                                            log.info("GasUsed: "+transactionReceipt.getGasUsed().toString());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    });

                            Subscription taskAcceptedEventSubscription = workerPool.taskAcceptedEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                                    .subscribe(taskAcceptedEvent ->{
                                        log.warn("SCHEDLR received taskAcceptedEvent"  + taskAcceptedEvent.taskID);
                                        log.warn("SCHEDLR choosing a random worker");
                                        log.warn("SCHEDLR calling pool for contribution of worker1 " + workerAddress);
                                        workerPool.callForContribution(taskAcceptedEvent.taskID, workerAddress, "");
                                    });

                            /*
                            Subscription callForContributionEventSubscription = workerPool.callForContributionEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                                    .subscribe(callForContributionEvent ->{
                                        log.warn("WORKER1 received callForContributionEvent for worker "+ callForContributionEvent.worker);
                                        if (callForContributionEvent.worker.equals(workerAddress)){
                                            log.warn("WORKER1 executing work");
                                            log.warn("WORKER1 contributing");
                                            workerPool.contribute(callForContributionEvent.taskID,  "resultHash".getBytes(), "resultSign".getBytes(), BigInteger.ZERO, "r".getBytes(), "s".getBytes());
                                        }
                                    });
                            //callForContributionEventSubscription.unsubscribe();

                            Subscription contributeEventSubscription = workerPool.contributeEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                                    .subscribe(contributeEvent ->{
                                        log.warn("SCHEDLR received contributeEvent " + contributeEvent.taskID);
                                        log.warn("SCHEDLR checking if consensus reached?");
                                        log.warn("SCHEDLR found consensus reached");
                                        log.warn("SCHEDLR reavealing consensus");
                                        workerPool.revealConsensus(contributeEvent.taskID,  "consensus".getBytes());
                                    });
                            //contributeEventSubscription.unsubscribe();

                            Subscription revealConsensusEventSubscription = workerPool.revealConsensusEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                                    .subscribe(revealConsensusEvent ->{
                                        log.warn("WORKER1 received revealConsensusEvent"+ revealConsensusEvent.taskID);
                                        log.warn("WORKER1 reavealing result");
                                        workerPool.reveal(revealConsensusEvent.taskID,  "result".getBytes());
                                    });
                            //revealConsensusEventSubscription.unsubscribe();

                            Subscription revealEventSubscription = workerPool.revealEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                                    .subscribe(revealEvent ->{
                                        log.warn("SCHEDLR received revealEvent ");
                                        log.warn("SCHEDLR checking if reveal timeout reached?");
                                        log.warn("SCHEDLR found reveal timeout reached");
                                        log.warn("SCHEDLR finalazing task");
                                        workerPool.finalizedTask(revealEvent.taskID,  "stdout", "stderr","http://uri");
                                    });
                            //revealEventSubscription.unsubscribe();



                        },
                        Throwable::printStackTrace);


    }*/
}
