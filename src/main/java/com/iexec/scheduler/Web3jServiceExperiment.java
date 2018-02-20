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
    public void workingExample() throws Exception {

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

        Subscription createWorkerPoolEventSubscription = iexecHubForScheduler.createWorkerPoolEventObservable(
                DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                .subscribe(createWorkerPoolEvent -> {
                            String workerPoolContractAddress = createWorkerPoolEvent.workerPool;
                            log.warn("SCHEDLR received createWorkerPoolEvent " + createWorkerPoolEvent.name + ":" + workerPoolContractAddress);

                        },
                        Throwable::printStackTrace);

        log.info("SCHEDLR creating workerPool");
        iexecHubForScheduler.createWorkerPool("myWorkerPool", BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO).send();
    }
}
