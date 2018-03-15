package com.iexec.scheduler.watcher;

import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.ethereum.EthConfig;
import com.iexec.scheduler.mock.MockConfig;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;

import static com.iexec.scheduler.ethereum.Utils.getStatus;

@Service
public class RevealWatcherService implements Reveal {

    private static final Logger log = LoggerFactory.getLogger(RevealWatcherService.class);
    private static final DefaultBlockParameterName END = DefaultBlockParameterName.LATEST;
    private final WorkerPoolService workerPoolService;
    private final MockConfig mockConfig;
    private final EthConfig ethConfig;

    @Autowired
    public RevealWatcherService(WorkerPoolService workerPoolService, MockConfig mockConfig, EthConfig ethConfig) {
        this.workerPoolService = workerPoolService;
        this.mockConfig = mockConfig;
        this.ethConfig = ethConfig;
    }

    @PostConstruct
    public void run() throws Exception {
        log.info("SCHEDLR watching RevealEvent (auto finalizeWork)");
        workerPoolService.getWorkerPool().revealEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(revealEvent -> {
                    onReveal(revealEvent);
                });
    }

    @Override
    public void onReveal(WorkerPool.RevealEventResponse revealEvent) {
        log.info("SCHEDLR received RevealEvent: " + Numeric.toHexString(revealEvent.result));
        log.info("SCHEDLR checking if reveal timeout reached?");
        log.info("SCHEDLR found reveal timeout reached");
        try {
            TransactionReceipt finalizedWorkReceipt = workerPoolService.getWorkerPool().finalizedWork(revealEvent.woid,
                    mockConfig.getFinalizeWork().getStdout(),
                    mockConfig.getFinalizeWork().getStderr(),
                    mockConfig.getFinalizeWork().getUri()).send();
            log.info("SCHEDLR finalize " + getStatus(finalizedWorkReceipt));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
