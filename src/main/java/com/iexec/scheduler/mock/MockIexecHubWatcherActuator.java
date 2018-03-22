package com.iexec.scheduler.mock;

import com.iexec.scheduler.actuator.ActuatorService;
import com.iexec.scheduler.database.ContributionService;
import com.iexec.scheduler.iexechub.IexecHubService;
import com.iexec.scheduler.iexechub.IexecHubWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MockIexecHubWatcherActuator implements IexecHubWatcher {

    private final IexecHubService iexecHubService;
    private final MockConfig mockConfig;
    private final ContributionService contributionService;
    private final ActuatorService actuatorService;
    private int subscribed;

    @Autowired
    public MockIexecHubWatcherActuator(IexecHubService iexecHubService, MockConfig mockConfig,
                                       ContributionService contributionService, ActuatorService actuatorService) {
        this.iexecHubService = iexecHubService;
        this.mockConfig = mockConfig;
        this.contributionService = contributionService;
        this.actuatorService = actuatorService;
        iexecHubService.register(this);
        subscribed = 0;
    }

    @Override
    public void onSubscription(String worker) {
        subscribed++;
        if (subscribed == mockConfig.getCallForContribution().getWorkers().size()) {
            actuatorService.emitMarketOrder(
                    mockConfig.getEmitMarketOrder().getCategory(),
                    mockConfig.getEmitMarketOrder().getTrust(),
                    mockConfig.getEmitMarketOrder().getValue(),
                    mockConfig.getEmitMarketOrder().getVolume()
            );
        }
    }

    @Override
    public void onWorkOrderActivated(String workOrderId) {
        contributionService.setCalledWorker(workOrderId, mockConfig.getCallForContribution().getWorkers());
        actuatorService.callForContributions(workOrderId,
                mockConfig.getCallForContribution().getWorkers(),
                mockConfig.getCallForContribution().getEnclaveChallenge()
        );
    }


}
