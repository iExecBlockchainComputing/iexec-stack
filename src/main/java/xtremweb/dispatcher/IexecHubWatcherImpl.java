package xtremweb.dispatcher;

import com.iexec.scheduler.actuator.ActuatorService;
import com.iexec.scheduler.database.ContributionService;
import com.iexec.scheduler.iexechub.IexecHubService;
import com.iexec.scheduler.iexechub.IexecHubWatcher;

public class IexecHubWatcherImpl implements IexecHubWatcher {

    private final static IexecHubService iexecHubService = IexecHubService.getInstance();
    private final static ContributionService contributionService = ContributionService.getInstance();
    private final static ActuatorService actuatorService = ActuatorService.getInstance();

    public IexecHubWatcherImpl() {
        iexecHubService.register(this);
    }

    @Override
    public void onSubscription(String worker) {

    }

    @Override
    public void onWorkOrderActivated(String workOrderId) {

    }
}
