package com.iexec.scheduler.mock;

import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.database.Contribution;
import com.iexec.scheduler.database.ContributionService;
import com.iexec.scheduler.actuator.*;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import com.iexec.scheduler.workerpool.WorkerPoolWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.iexec.scheduler.ethereum.Utils.hashResult;

@Service
public class MockWorkerPoolWatcherActuator implements WorkerPoolWatcher {

    private static final Logger log = LoggerFactory.getLogger(MockWorkerPoolWatcherActuator.class);
    private final WorkerPoolService workerPoolService;
    private final MockConfig mockConfig;
    private final ContributionService contributionService;
    private final ActuatorService actuatorService;
    private List<String> revealed;

    @Autowired
    public MockWorkerPoolWatcherActuator(WorkerPoolService workerPoolService, MockConfig mockConfig,
                                         ContributionService contributionService,
                                         ActuatorService actuatorService) {
        this.workerPoolService = workerPoolService;
        this.mockConfig = mockConfig;
        this.contributionService = contributionService;
        this.actuatorService = actuatorService;
        revealed = new ArrayList<>();
        workerPoolService.registerWorkerPoolWatcher(this);
    }


    @Override
    public void onContributeEvent(WorkerPool.ContributeEventResponse contributeEvent) {
        Contribution contribution = new Contribution(contributeEvent.woid, contributeEvent.worker, contributeEvent.resultHash);
        contributionService.addContribution(contribution);
        log.info("SCHEDLR checking if consensus worker contributed (or timeout) reached?");
        if (contributionService.hasAllWorkerContributed(contributeEvent.woid)) { //TODO - add contribute timeout
            actuatorService.revealConsensus(contributeEvent, hashResult(mockConfig.getWorkerResult()));
        }
    }

    @Override
    public void onReveal(WorkerPool.RevealEventResponse revealEvent) {
        if (!revealed.contains(revealEvent.woid)){
            revealed.add(revealEvent.woid);
            log.info("SCHEDLR finalizeWork");
            actuatorService.finalizeWork(revealEvent, mockConfig.getFinalizeWork().getStdout(),
                    mockConfig.getFinalizeWork().getStderr(),
                    mockConfig.getFinalizeWork().getUri());
        }
    }


}
