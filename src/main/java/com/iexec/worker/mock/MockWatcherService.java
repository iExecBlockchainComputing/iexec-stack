package com.iexec.worker.mock;

import com.iexec.worker.ethereum.CredentialsService;
import com.iexec.worker.ethereum.EthConfig;
import com.iexec.worker.ethereum.RlcService;
import com.iexec.worker.iexechub.IexecHubService;
import com.iexec.worker.workerpool.WorkerPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Hash;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
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
                              IexecHubService iexecHubService, WorkerPoolService workerPoolService) {
        this.credentialsService = credentialsService;
        this.iexecHubService = iexecHubService;
        this.workerPoolService = workerPoolService;
        this.rlcService = rlcService;
        this.ethConfig = ethConfig;
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
            TransactionReceipt subscriptionDepositReceipt = iexecHubService.getIexecHub().deposit(BigInteger.valueOf(10)).send();
            log.info("WORKER1 subscriptionDeposit " + getStatus(subscriptionDepositReceipt));
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

                        try {
                            TransactionReceipt contributeDepositReceipt = iexecHubService.getIexecHub().deposit(BigInteger.valueOf(30)).send();
                            log.info("WORKER1 contributeDeposit " + getStatus(contributeDepositReceipt));
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
