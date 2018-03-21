package com.iexec.scheduler.mock;

import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.database.ContributionService;
import com.iexec.scheduler.actuator.*;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import com.iexec.scheduler.workerpool.WorkerPoolWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.iexec.scheduler.ethereum.Utils.hashResult;

@Service
public class MockWorkerPoolWatcherActuator implements WorkerPoolWatcher {

    private static final Logger log = LoggerFactory.getLogger(MockWorkerPoolWatcherActuator.class);
    private final MockConfig mockConfig;
    private final ContributionService contributionService;
    private final ActuatorService actuatorService;

    @Autowired
    public MockWorkerPoolWatcherActuator(WorkerPoolService workerPoolService, MockConfig mockConfig,
                                         ContributionService contributionService, ActuatorService actuatorService) {
        this.mockConfig = mockConfig;
        this.contributionService = contributionService;
        this.actuatorService = actuatorService;
        workerPoolService.register(this);
    }


    @Override
    public void onContributeEvent(WorkerPool.ContributeEventResponse contributeEvent) {
        contributionService.addContributionToMap(contributeEvent);
        if (!contributionService.isConsensusReached(contributeEvent)) {
            return;
        }
        actuatorService.revealConsensus(contributeEvent, hashResult(mockConfig.getWorkerResult()));
    }

    @Override
    public void onReveal(WorkerPool.RevealEventResponse revealEvent) {
        log.debug("SCHEDLR checking if reveal timeout reached?");
        actuatorService.finalizeWork(revealEvent, mockConfig.getFinalizeWork().getStdout(),
                mockConfig.getFinalizeWork().getStderr(),
                mockConfig.getFinalizeWork().getUri());
    }
}
