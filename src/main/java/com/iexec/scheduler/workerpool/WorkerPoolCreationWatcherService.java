package com.iexec.scheduler.workerpool;

import com.iexec.scheduler.contracts.generated.IexecHub;
import com.iexec.scheduler.ethereum.EthConfig;
import com.iexec.scheduler.iexechub.IexecHubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.DefaultBlockParameterName;
import rx.Observable;

import javax.annotation.PostConstruct;

@Service
public class WorkerPoolCreationWatcherService {

    private static final Logger log = LoggerFactory.getLogger(WorkerPoolCreationWatcherService.class);
    private final IexecHubService iexecHubService;
    private final WorkerPoolConfig poolConfig;
    private final EthConfig ethConfig;
    private String workerPoolAddress;

    @Autowired
    public WorkerPoolCreationWatcherService(IexecHubService iexecHubService, WorkerPoolConfig poolConfig, EthConfig ethConfig) {
        this.iexecHubService = iexecHubService;
        this.poolConfig = poolConfig;
        this.ethConfig = ethConfig;
    }

    @PostConstruct
    public void run() throws Exception {
        if (poolConfig.getAddress().isEmpty()) {
            log.info("SCHEDLR watching CreateWorkerPoolEvent ");
            Observable<IexecHub.CreateWorkerPoolEventResponse> createWorkerPoolEventResponseObservable = iexecHubService.getIexecHub().createWorkerPoolEventObservable(ethConfig.getStartBlockParameter(), DefaultBlockParameterName.LATEST);
            IexecHub.CreateWorkerPoolEventResponse createWorkerPoolEvent = createWorkerPoolEventResponseObservable.toBlocking().first();
            this.workerPoolAddress = createWorkerPoolEvent.workerPool;
            log.info("SCHEDLR received CreateWorkerPoolEvent " + createWorkerPoolEvent.workerPoolName + ":" + createWorkerPoolEvent.workerPool);
        }
    }

    public String getWorkerPoolAddress() {
        return workerPoolAddress;
    }

}
