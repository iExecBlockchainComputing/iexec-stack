package com.iexec.scheduler.workerpool;

import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.ethereum.TransactionError;

public interface WorkerPoolWatcher {

    void onContributeEvent(WorkerPool.ContributeEventResponse contributeEvent);
    void onReveal(WorkerPool.RevealEventResponse revealEvent);
}
