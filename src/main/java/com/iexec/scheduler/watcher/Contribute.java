package com.iexec.scheduler.watcher;

import com.iexec.scheduler.contracts.generated.WorkerPool;

public interface Contribute {

    void onContributeEvent(WorkerPool.ContributeEventResponse contributeEvent);
}
