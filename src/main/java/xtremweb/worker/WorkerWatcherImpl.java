package xtremweb.worker;


import com.iexec.worker.actuator.ActuatorService;
import com.iexec.worker.workerpool.WorkerPoolService;
import com.iexec.worker.workerpool.WorkerPoolWatcher;

public class WorkerWatcherImpl implements WorkerPoolWatcher {

    private final static WorkerPoolService workerPoolService = WorkerPoolService.getInstance();
    private final static ActuatorService actuatorService = ActuatorService.getInstance();

    public WorkerWatcherImpl() {
        workerPoolService.registerWorkerPoolWatcher(this);
        actuatorService.subscribeToPool();
    }


    @Override
    public void onRevealConsensus(String workOrderId) {

    }

    @Override
    public void onCallForContribution(String workOrderId) {

    }
}
