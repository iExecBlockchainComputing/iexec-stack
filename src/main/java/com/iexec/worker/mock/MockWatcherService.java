package com.iexec.worker.mock;

import com.iexec.worker.contracts.generated.IexecHub;
import com.iexec.worker.contracts.generated.WorkOrder;
import com.iexec.worker.ethereum.CredentialsService;
import com.iexec.worker.ethereum.EthConfig;
import com.iexec.worker.ethereum.RlcService;
import com.iexec.worker.iexechub.IexecHubService;
import com.iexec.worker.scheduler.SchedulerApiService;
import com.iexec.worker.workerpool.WorkerPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;

import static com.iexec.worker.ethereum.Utils.*;

@Service
public class MockWatcherService {

    private static final Logger log = LoggerFactory.getLogger(MockWatcherService.class);
    private static final DefaultBlockParameterName END = DefaultBlockParameterName.LATEST;
    private final CredentialsService credentialsService;
    private final IexecHubService iexecHubService;
    private final WorkerPoolService workerPoolService;
    private final RlcService rlcService;
    private final EthConfig ethConfig;
    private final SchedulerApiService schedulerApiService;
    private final Web3j web3j;

    //Mock values
    @Value("${mock.worker-result}")
    private String workerResult;
    @Value("${mock.contribute.v}")
    private BigInteger contributeV;
    @Value("${mock.contribute.r}")
    private String contributeR;
    @Value("${mock.contribute.s}")
    private String contributeS;

    @Autowired
    public MockWatcherService(CredentialsService credentialsService, EthConfig ethConfig, RlcService rlcService,
                              IexecHubService iexecHubService, WorkerPoolService workerPoolService,
                              SchedulerApiService schedulerApiService, Web3j web3j) {
        this.credentialsService = credentialsService;
        this.iexecHubService = iexecHubService;
        this.workerPoolService = workerPoolService;
        this.rlcService = rlcService;
        this.ethConfig = ethConfig;
        this.schedulerApiService = schedulerApiService;
        this.web3j = web3j;
    }

    @PostConstruct
    public void run() throws Exception {
        allowRlc();
        subscribeToWorkerPool();
        watchCallForContributionAndContribute();
        watchRevealConsensusAndReveal();
    }

    private void allowRlc() {
        try {
            TransactionReceipt approveReceipt = rlcService.getRlc().approve(iexecHubService.getIexecHub().getContractAddress(), BigInteger.valueOf(100)).send();
            log.info("WORKER1 approve " + getStatus(approveReceipt));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void subscribeToWorkerPool() {
        try {
            TransactionReceipt subscriptionDepositReceipt = iexecHubService.getIexecHub().deposit(schedulerApiService.getWorkerPoolPolicy().getSubscriptionMinimumStakePolicy()).send();
            log.info("WORKER1 subscriptionDeposit " + schedulerApiService.getWorkerPoolPolicy().getSubscriptionMinimumStakePolicy() + " " + getStatus(subscriptionDepositReceipt));
            TransactionReceipt subscribeToPoolReceipt = workerPoolService.getWorkerPool().subscribeToPool().send();
            log.info("WORKER1 subscribeToPool " + getStatus(subscribeToPoolReceipt));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void watchCallForContributionAndContribute() {
        log.info("WORKER1 watching CallForContributionEvent (auto contribute)");
        workerPoolService.getWorkerPool().callForContributionEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(callForContributionEvent -> {
                    if (callForContributionEvent.worker.equals(credentialsService.getCredentials().getAddress())) {
                        log.info("WORKER1 received callForContributionEvent for worker " + callForContributionEvent.worker);
                        log.info("WORKER1 executing work");
                        String hashResult = hashResult(workerResult);
                        String signResult = signByteResult(workerResult, credentialsService.getCredentials().getAddress());

                        log.info("WORKER1 found hashResult " + hashResult);
                        log.info("WORKER1 found signResult " + signResult);

                        byte[] hashResultBytes = Numeric.hexStringToByteArray(hashResult);
                        byte[] hashSignBytes = Numeric.hexStringToByteArray(signResult);
                        byte[] r = Numeric.hexStringToByteArray(asciiToHex(contributeR));
                        byte[] s = Numeric.hexStringToByteArray(asciiToHex(contributeS));

                        WorkOrder workOrder = WorkOrder.load(
                                callForContributionEvent.woid, web3j, credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

                        try {
                            BigInteger m_emitcost = workOrder.m_emitcost().send();
                            log.info("emitcost " + m_emitcost);
                            m_emitcost = BigInteger.valueOf(100); //TODO - Check why emitcost==0

                            Float deposit = (schedulerApiService.getWorkerPoolPolicy().getStakeRatioPolicy().floatValue()/100)*m_emitcost.floatValue();//(30/100)*100
                            BigInteger depositBig = BigDecimal.valueOf(deposit).toBigInteger();
                            TransactionReceipt contributeDepositReceipt = iexecHubService.getIexecHub().deposit(depositBig).send();
                            log.info("WORKER1 contributeDeposit " + depositBig + " " + getStatus(contributeDepositReceipt));
                            TransactionReceipt contributeReceipt = workerPoolService.getWorkerPool().contribute(callForContributionEvent.woid, hashResultBytes, hashSignBytes, contributeV, r, s).send();
                            log.info("WORKER1 contribute " + getStatus(contributeReceipt));
                            log.info(contributeReceipt.getTransactionHash());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void watchRevealConsensusAndReveal() {
        log.info("WORKER1 watching RevealConsensusEvent (auto reveal)");
        workerPoolService.getWorkerPool().revealConsensusEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(revealConsensusEvent -> {
                    log.info("WORKER1 received revealConsensusEvent " + revealConsensusEvent.woid);
                    byte[] result = Numeric.hexStringToByteArray(Hash.sha3String(workerResult));
                    try {
                        TransactionReceipt revealReceipt = workerPoolService.getWorkerPool().reveal(revealConsensusEvent.woid, result).send();
                        log.info("WORKER1 reveal " + Hash.sha3String(workerResult) + " " + getStatus(revealReceipt));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }


}
