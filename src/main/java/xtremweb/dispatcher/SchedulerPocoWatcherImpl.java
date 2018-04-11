package xtremweb.dispatcher;

import com.iexec.common.contracts.generated.WorkerPool;
import com.iexec.common.ethereum.Utils;
import com.iexec.scheduler.actuator.ActuatorService;
import com.iexec.scheduler.database.ContributionService;
import com.iexec.scheduler.iexechub.IexecHubService;
import com.iexec.scheduler.iexechub.IexecHubWatcher;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import com.iexec.scheduler.workerpool.WorkerPoolWatcher;

import java.math.BigInteger;
import java.util.Arrays;

public class SchedulerPocoWatcherImpl implements IexecHubWatcher, WorkerPoolWatcher {

    private final static IexecHubService iexecHubService = IexecHubService.getInstance();
    private final static WorkerPoolService workerPoolService = WorkerPoolService.getInstance();
    private final static ContributionService contributionService = ContributionService.getInstance();
    private final static ActuatorService actuatorService = ActuatorService.getInstance();

    public SchedulerPocoWatcherImpl() {
        iexecHubService.registerIexecHubWatcher(this);
        workerPoolService.registerWorkerPoolWatcher(this);
    }

    @Override
    public void onSubscription(String worker) {
        //actuatorService.createMarketOrder(BigInteger.ONE, BigInteger.ZERO, BigInteger.valueOf(100), BigInteger.ONE); //on N worker alive
    }

    @Override
    public void onWorkOrderActivated(String workOrderId) {
        //actuatorService.allowWorkersToContribute(workOrderId, Arrays.asList("0x70a1bebd73aef241154ea353d6c8c52d420d4f5b"), "O");
    }

    @Override
    public void onContributeEvent(WorkerPool.ContributeEventResponse contributeEventResponse) {
        //actuatorService.revealConsensus(contributeEventResponse.woid, Utils.hashResult("iExec the wanderer"));
    }

    @Override
    public void onReveal(WorkerPool.RevealEventResponse revealEventResponse) {
        //actuatorService.finalizeWork(revealEventResponse.woid,"aStdout", "aStderr", "anUri");
    }
}
