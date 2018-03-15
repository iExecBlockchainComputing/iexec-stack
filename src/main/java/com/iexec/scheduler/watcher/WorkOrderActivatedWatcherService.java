package com.iexec.scheduler.watcher;

import com.iexec.scheduler.contracts.generated.IexecHub;
import com.iexec.scheduler.ethereum.EthConfig;
import com.iexec.scheduler.ethereum.RlcService;
import com.iexec.scheduler.iexechub.IexecHubService;
import com.iexec.scheduler.marketplace.MarketplaceService;
import com.iexec.scheduler.mock.MockConfig;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import javax.annotation.PostConstruct;

import static com.iexec.scheduler.ethereum.Utils.getStatus;

@Service
public class WorkOrderActivatedWatcherService implements WorkOrderActivated {

    private static final Logger log = LoggerFactory.getLogger(WorkOrderActivatedWatcherService.class);
    private static final DefaultBlockParameterName END = DefaultBlockParameterName.LATEST;
    private final IexecHubService iexecHubService;
    private final WorkerPoolService workerPoolService;
    private final MarketplaceService marketplaceService;
    private final RlcService rlcService;
    private final MockConfig mockConfig;
    private final EthConfig ethConfig;
    private final ContributionMapService contributionMapService;

    @Autowired
    public WorkOrderActivatedWatcherService(IexecHubService iexecHubService, WorkerPoolService workerPoolService,
                                            MarketplaceService marketplaceService, RlcService rlcService,
                                            MockConfig mockConfig, EthConfig ethConfig, ContributionMapService contributionMapService) {
        this.iexecHubService = iexecHubService;
        this.workerPoolService = workerPoolService;
        this.marketplaceService = marketplaceService;
        this.rlcService = rlcService;
        this.mockConfig = mockConfig;
        this.ethConfig = ethConfig;
        this.contributionMapService = contributionMapService;
    }

    @PostConstruct
    public void run() throws Exception {
        log.info("SCHEDLR watching workOrderActivatedEvent (auto callForContribution)");
        iexecHubService.getIexecHub().workOrderActivatedEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(this::onWorkOrderActivated);
    }

    @Override
    public void onWorkOrderActivated(IexecHub.WorkOrderActivatedEventResponse workOrderActivatedEvent) {
        log.info("SCHEDLR received workOrderActivatedEvent " + workOrderActivatedEvent.woid);
        contributionMapService.setupForFutureContributions(workOrderActivatedEvent);
        try {
            TransactionReceipt callForContributionsReceipt = workerPoolService.getWorkerPool()
                    .callForContributions(workOrderActivatedEvent.woid,
                            mockConfig.getCallForContribution().getWorkers(),
                            mockConfig.getCallForContribution().getEnclaveChallenge()).send();
            log.info("SCHEDLR callForContributions " + getStatus(callForContributionsReceipt)
                    + " of workers " + mockConfig.getCallForContribution().getWorkers().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
