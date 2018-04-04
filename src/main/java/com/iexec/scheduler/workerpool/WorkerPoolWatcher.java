package com.iexec.scheduler.workerpool;


import com.iexec.common.contracts.generated.WorkerPool;

public interface WorkerPoolWatcher {

    void onContributeEvent(WorkerPool.ContributeEventResponse contributeEvent);
    void onReveal(WorkerPool.RevealEventResponse revealEvent);
}
