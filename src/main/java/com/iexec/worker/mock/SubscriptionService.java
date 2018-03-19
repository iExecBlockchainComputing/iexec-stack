package com.iexec.worker.mock;

import com.iexec.worker.ethereum.RlcService;
import com.iexec.worker.iexechub.IexecHubService;
import com.iexec.worker.scheduler.SchedulerApiService;
import com.iexec.worker.workerpool.WorkerPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import javax.annotation.PostConstruct;
import java.math.BigInteger;

import static com.iexec.worker.ethereum.Utils.getStatus;

@Service
public class SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);
    private final IexecHubService iexecHubService;
    private final WorkerPoolService workerPoolService;
    private final RlcService rlcService;
    private final SchedulerApiService schedulerApiService;

    @Autowired
    public SubscriptionService(IexecHubService iexecHubService, WorkerPoolService workerPoolService,
                               RlcService rlcService, SchedulerApiService schedulerApiService) {
        this.iexecHubService = iexecHubService;
        this.workerPoolService = workerPoolService;
        this.rlcService = rlcService;
        this.schedulerApiService = schedulerApiService;
    }

    @PostConstruct
    public void run() throws Exception {
        TransactionReceipt approveReceipt = rlcService.getRlc().approve(iexecHubService.getIexecHub().getContractAddress(), BigInteger.valueOf(100)).send();
        log.info("WORKER1 approve " + getStatus(approveReceipt));
        TransactionReceipt subscriptionDepositReceipt = iexecHubService.getIexecHub().deposit(schedulerApiService.getWorkerPoolPolicy().getSubscriptionMinimumStakePolicy()).send();
        log.info("WORKER1 subscriptionDeposit " + schedulerApiService.getWorkerPoolPolicy().getSubscriptionMinimumStakePolicy() + " " + getStatus(subscriptionDepositReceipt));
        TransactionReceipt subscribeToPoolReceipt = workerPoolService.getWorkerPool().subscribeToPool().send();
        log.info("WORKER1 subscribeToPool " + getStatus(subscribeToPoolReceipt));
    }


}
