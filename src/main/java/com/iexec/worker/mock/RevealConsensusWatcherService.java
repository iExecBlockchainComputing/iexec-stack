package com.iexec.worker.mock;

import com.iexec.worker.contracts.generated.WorkerPool;
import com.iexec.worker.ethereum.EthConfig;
import com.iexec.worker.workerpool.WorkerPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Hash;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;

import static com.iexec.worker.ethereum.Utils.getStatus;

@Service
public class RevealConsensusWatcherService implements RevealConsensus{

    private static final Logger log = LoggerFactory.getLogger(RevealConsensusWatcherService.class);
    private static final DefaultBlockParameterName END = DefaultBlockParameterName.LATEST;
    private final WorkerPoolService workerPoolService;
    private final EthConfig ethConfig;

    @Value("${mock.worker-result}")
    private String workerResult;

    @Autowired
    public RevealConsensusWatcherService(WorkerPoolService workerPoolService, EthConfig ethConfig,
                                         SubscriptionService subscriptionService) {
        this.workerPoolService = workerPoolService;
        this.ethConfig = ethConfig;
    }

    @PostConstruct
    public void run() throws Exception {
        log.info("WORKER1 watching RevealConsensusEvent (auto reveal)");
        workerPoolService.getWorkerPool().revealConsensusEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(this::onRevealConsensus);
    }

    @Override
    public void onRevealConsensus(WorkerPool.RevealConsensusEventResponse revealConsensusEvent) {
        log.info("WORKER1 received revealConsensusEvent " + revealConsensusEvent.woid);
        byte[] result = Numeric.hexStringToByteArray(Hash.sha3String(workerResult));
        TransactionReceipt revealReceipt = null;
        try {
            revealReceipt = workerPoolService.getWorkerPool().reveal(revealConsensusEvent.woid, result).send();
            log.info("WORKER1 reveal " + Hash.sha3String(workerResult) + " " + getStatus(revealReceipt));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
