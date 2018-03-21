package com.iexec.scheduler.iexechub;

public interface IexecHubWatcher {

    void onSubscription(String worker);
    void woid(String workOrderActivatedEvent);
}
