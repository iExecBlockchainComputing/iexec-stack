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
import static com.iexec.scheduler.ethereum.Utils.hashResult;

@Service
public class ContributeWatcherService implements Contribute {

    private static final Logger log = LoggerFactory.getLogger(ContributeWatcherService.class);
    private static final DefaultBlockParameterName END = DefaultBlockParameterName.LATEST;
    private final WorkerPoolService workerPoolService;
    private final MockConfig mockConfig;
    private final EthConfig ethConfig;
    private final ContributionMapService contributionMapService;

    @Autowired
    public ContributeWatcherService(WorkerPoolService workerPoolService, ContributionMapService contributionMapService,
                                    MockConfig mockConfig, EthConfig ethConfig) {
        this.workerPoolService = workerPoolService;
        this.mockConfig = mockConfig;
        this.ethConfig = ethConfig;
        this.contributionMapService = contributionMapService;
    }

    @PostConstruct
    public void run() throws Exception {
        log.info("SCHEDLR watching ContributeEvent (auto revealConsensus)");
        workerPoolService.getWorkerPool().contributeEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(this::onContributeEvent);
    }

    @Override
    public void onContributeEvent(WorkerPool.ContributeEventResponse contributeEvent) {
        log.info("SCHEDLR received ContributeEvent " + contributeEvent.woid + " of worker " + contributeEvent.worker);
        //TODO - Change logic here
        contributionMapService.addContributionToMap(contributeEvent);
        if (!contributionMapService.isConsensusReached(contributeEvent)) {
            return;
        }
        byte[] consensus = Numeric.hexStringToByteArray(hashResult(mockConfig.getWorkerResult()));
        try {
            TransactionReceipt revealConsensusReceipt = workerPoolService.getWorkerPool()
                    .revealConsensus(contributeEvent.woid, consensus).send();
            log.info("SCHEDLR revealConsensus " + hashResult(mockConfig.getWorkerResult()) + " "
                    + getStatus(revealConsensusReceipt));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
