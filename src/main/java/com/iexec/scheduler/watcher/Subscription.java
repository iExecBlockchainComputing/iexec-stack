package com.iexec.scheduler.watcher;

import com.iexec.scheduler.contracts.generated.IexecHub;

public interface Subscription {

    void onSubscription(IexecHub.WorkerPoolSubscriptionEventResponse workerPoolSubscriptionEvent);
}
