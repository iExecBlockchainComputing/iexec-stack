package com.iexec.scheduler.workerpool;


import com.iexec.common.contracts.generated.WorkerPool;

public interface WorkerPoolWatcher {

    void onContributeEvent(WorkerPool.ContributeEventResponse contributeEvent);

    void onReveal(WorkerPool.RevealEventResponse revealEvent);

    void onWorkOrderClaimed(WorkerPool.WorkOrderClaimedEventResponse workOrderClaimedEvent);

    void onReopenEvent(WorkerPool.ReopenEventResponse reopenEvent);

    void onWorkerEvictionEvent(WorkerPool.WorkerEvictionEventResponse workerEvictionEvent);
}
