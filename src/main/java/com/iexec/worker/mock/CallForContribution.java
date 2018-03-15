package com.iexec.worker.mock;

import com.iexec.worker.contracts.generated.WorkerPool;

public interface CallForContribution {

    void onCallForContribution(WorkerPool.CallForContributionEventResponse callForContributionEvent);
}
