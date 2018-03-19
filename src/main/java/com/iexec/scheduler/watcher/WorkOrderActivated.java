package com.iexec.scheduler.watcher;

import com.iexec.scheduler.contracts.generated.IexecHub;

public interface WorkOrderActivated {

    void onWorkOrderActivated(IexecHub.WorkOrderActivatedEventResponse workOrderActivatedEvent);
}
