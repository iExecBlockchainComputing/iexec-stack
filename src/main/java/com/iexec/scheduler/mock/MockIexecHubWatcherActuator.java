package com.iexec.scheduler.mock;

import com.iexec.scheduler.contracts.generated.IexecHub;
import com.iexec.scheduler.database.ContributionMapService;
import com.iexec.scheduler.actuator.ActuatorService;
import com.iexec.scheduler.iexechub.IexecHubService;
import com.iexec.scheduler.iexechub.IexecHubWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MockIexecHubWatcherActuator implements IexecHubWatcher {

    private final IexecHubService iexecHubService;
    private final MockConfig mockConfig;
    private final ContributionMapService contributionMapService;
    private final ActuatorService actuatorService;

    @Autowired
    public MockIexecHubWatcherActuator(IexecHubService iexecHubService, MockConfig mockConfig,
                                       ContributionMapService contributionMapService, ActuatorService actuatorService) {
        this.iexecHubService = iexecHubService;
        this.mockConfig = mockConfig;
        this.contributionMapService = contributionMapService;
        this.actuatorService = actuatorService;
        iexecHubService.register(this);
    }

    @Override
    public void onSubscription(String worker) {
        actuatorService.emitMarketOrder(
                mockConfig.getEmitMarketOrder().getCategory(),
                mockConfig.getEmitMarketOrder().getTrust(),
                mockConfig.getEmitMarketOrder().getValue(),
                mockConfig.getEmitMarketOrder().getVolume()
        );
    }

    @Override
    public void woid(String woid) {
        contributionMapService.setupForFutureContributions(woid);
        actuatorService.callForContributions(woid,
                mockConfig.getCallForContribution().getWorkers(),
                mockConfig.getCallForContribution().getEnclaveChallenge()
        );
    }


}
