package com.iexec.scheduler.watcher;

import com.iexec.scheduler.contracts.generated.IexecHub;
import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.mock.MockConfig;
import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.tuples.generated.Tuple2;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static com.iexec.scheduler.ethereum.Utils.EMPTY_BYTE;

@Service
public class ContributionMapService {

    private static final Logger log = LoggerFactory.getLogger(ContributionMapService.class);

    private final MockConfig mockConfig;
    private Map<Tuple2<String, String>, byte[]> contributionMap;

    @Autowired
    public ContributionMapService(MockConfig mockConfig) {
        this.mockConfig = mockConfig;
        this.contributionMap = new HashMap<>();
    }

    @PostConstruct
    public void run() throws Exception {

    }

    public void setupForFutureContributions(IexecHub.WorkOrderActivatedEventResponse workOrderActivatedEvent) {
        for (String worker : mockConfig.getCallForContribution().getWorkers()) {
            Tuple2<String, String> orderWorkerTuple = new Tuple2<>(workOrderActivatedEvent.woid, worker);
            contributionMap.put(orderWorkerTuple, EMPTY_BYTE);
        }
    }

    public void addContributionToMap(WorkerPool.ContributeEventResponse contributeEvent) {
        Tuple2<String, String> orderWorkerTuple = new Tuple2<>(contributeEvent.woid, contributeEvent.worker);
        if (contributionMap.containsKey(orderWorkerTuple)) {
            contributionMap.replace(orderWorkerTuple, contributeEvent.resultHash);
        }
    }

    public boolean isConsensusReached(WorkerPool.ContributeEventResponse contributeEvent) {
        log.info("SCHEDLR checking if consensus reached?");
        for (String worker : mockConfig.getCallForContribution().getWorkers()) {
            Tuple2<String, String> orderWorkerTuple = new Tuple2<>(contributeEvent.woid, worker);
            if (Arrays.areEqual(contributionMap.get(orderWorkerTuple), EMPTY_BYTE)) {
                return false;
            }
        }
        log.info("SCHEDLR found consensus reached");
        return true;
    }

}
