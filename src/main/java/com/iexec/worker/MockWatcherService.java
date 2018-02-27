package com.iexec.worker;

import com.iexec.worker.contracts.generated.WorkerPool;
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
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;

import static com.iexec.worker.Utils.asciiToHex;
import static com.iexec.worker.Utils.hashResult;
import static com.iexec.worker.Utils.signByteResult;

@Service
public class MockWatcherService {

    private static final Logger log = LoggerFactory.getLogger(MockWatcherService.class);

    private static final DefaultBlockParameterName END = DefaultBlockParameterName.LATEST;
    private static final String WORKER_RESULT = "iExec the wanderer";

    private Web3j web3j;
    private Credentials workerCredentials;
    private String workerPoolAddress;
    private WorkerPool workerPool;


    @Value("${ethereum.start-block}")
    private BigInteger startBlock;


    @Value("${wallet.folder}")
    private String walletFolder;

    @Value("${worker.address}")
    private String workerAdress;

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
        subscribeToWorkerPool();
        watchCallForContributionAndContribute();
        watchRevealConsensusAndReveal();

    }

    private void init() throws IOException, CipherException {
        log.info("Connected to Ethereum client version: " + web3j.web3ClientVersion().send().getWeb3ClientVersion());

        log.info("Loading credentials");
        workerCredentials = WalletUtils.loadCredentials(workerWalletPassword, walletFolder + "/" + workerWalletFilename);

        log.info("Loading smart contracts");
        workerPool = WorkerPool.load(
                workerPoolAddress, web3j, workerCredentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    private void subscribeToWorkerPool() {
        log.info("subscribeToWorkerPool");
        try {
            log.info("WORKER1 subscribing to workerPool");
            log.info(workerPool.subscribeToPool().send().getGasUsed().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void watchCallForContributionAndContribute() {
        log.info("watchCallForContributionAndContribute");
        workerPool.callForContributionEventObservable(getStartBlock(), END)
                .subscribe(callForContributionEvent -> {
                    log.warn("WORKER1 received callForContributionEvent for worker " + callForContributionEvent.worker);
                    log.warn("WORKER1 executing work");
                    log.warn("WORKER1 contributing");

                    String hashResult = hashResult(WORKER_RESULT);
                    String signResult = signByteResult(WORKER_RESULT, workerAdress);

                    log.info("WORKER1 found hashResult " + hashResult);
                    log.info("WORKER1 found signResult " + signResult);

                    byte[] hashResultBytes = Numeric.hexStringToByteArray(hashResult);
                    byte[] hashSignBytes = Numeric.hexStringToByteArray(signResult);
                    byte[] r = Numeric.hexStringToByteArray(asciiToHex("0"));

                    try {
                        workerPool.contribute(callForContributionEvent.woid, hashResultBytes, hashSignBytes, BigInteger.ZERO, r, r).send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }


    private void watchRevealConsensusAndReveal() {
        log.info("watchRevealConsensusAndReveal");
        workerPool.revealConsensusEventObservable(getStartBlock(), END)
                .subscribe(revealConsensusEvent -> {
                    log.warn("WORKER1 received revealConsensusEvent " + revealConsensusEvent.woid);
                    log.warn("WORKER1 reavealing WORKER_RESULT");
                    byte[] result = Numeric.hexStringToByteArray(Hash.sha3String(WORKER_RESULT));
                    workerPool.reveal(revealConsensusEvent.woid, result);
                });
    }



    public String getWorkerPoolAddress() {
        return workerPoolAddress;
    }


    private DefaultBlockParameter getStartBlock() {
        return DefaultBlockParameter.valueOf(startBlock);
    }
}
