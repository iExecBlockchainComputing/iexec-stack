package com.iexec.scheduler.service;

import com.iexec.scheduler.contracts.generated.IexecHub;
import com.iexec.scheduler.contracts.generated.WorkerPool;
import com.iexec.scheduler.helper.WorkerPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import rx.Observable;
import rx.Subscription;

import javax.annotation.PostConstruct;
import java.math.BigInteger;

@Service
public class WorkerPoolCreationWatcherService {

    private static final Logger log = LoggerFactory.getLogger(WorkerPoolCreationWatcherService.class);

    private final IexecHubService iexecHubService;
    private String workerPoolAddress;

    @Value("${ethereum.startBlock}")
    private BigInteger startBlock;

    @Autowired
    public WorkerPoolCreationWatcherService(IexecHubService iexecHubService) {
        this.iexecHubService = iexecHubService;
    }

    @PostConstruct
    public void run() throws Exception {
        log.info("SCHEDLR watching CreateWorkerPoolEvent");

        Observable<IexecHub.CreateWorkerPoolEventResponse> o = iexecHubService.getIexecHub().createWorkerPoolEventObservable(getStartBlock(), DefaultBlockParameterName.LATEST);
        IexecHub.CreateWorkerPoolEventResponse createWorkerPoolEvent = o.toBlocking().first();
        this.workerPoolAddress =  createWorkerPoolEvent.workerPool;
        log.warn("SCHEDLR received CreateWorkerPoolEvent " + createWorkerPoolEvent.workerPoolName + ":" + createWorkerPoolEvent.workerPool);


       /*Subscription s = o
                .subscribe(createWorkerPoolEvent -> {
                    //if (createWorkerPoolEvent.workerPoolName.equals(poolConfig.getName())) {
                    log.warn("SCHEDLR received CreateWorkerPoolEvent " + createWorkerPoolEvent.workerPoolName + ":" + createWorkerPoolEvent.workerPool);
                    workerPoolAddress = createWorkerPoolEvent.workerPool;
                    //WorkerPool workerPool = loadWorkerPool(createWorkerPoolEvent.workerPool);
                    //setupWorkerPool(workerPool);
                    //}
                });*/

    }

    public String getWorkerPoolAddress() {
        return workerPoolAddress;
    }

    private DefaultBlockParameter getStartBlock() {
        return DefaultBlockParameter.valueOf(startBlock);
    }

}
