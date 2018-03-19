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
import java.math.BigDecimal;
import java.math.BigInteger;

import static com.iexec.scheduler.ethereum.Utils.getStatus;

@Service
public class SubscriptionWatcherService implements Subscription {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionWatcherService.class);
    private static final DefaultBlockParameterName END = DefaultBlockParameterName.LATEST;
    private final IexecHubService iexecHubService;
    private final WorkerPoolService workerPoolService;
    private final MarketplaceService marketplaceService;
    private final RlcService rlcService;
    private final MockConfig mockConfig;
    private final EthConfig ethConfig;
    private boolean workerSubscribed;

    @Autowired
    public SubscriptionWatcherService(IexecHubService iexecHubService, WorkerPoolService workerPoolService,
                                      MarketplaceService marketplaceService, RlcService rlcService,
                                      MockConfig mockConfig, EthConfig ethConfig) {
        this.iexecHubService = iexecHubService;
        this.workerPoolService = workerPoolService;
        this.marketplaceService = marketplaceService;
        this.rlcService = rlcService;
        this.mockConfig = mockConfig;
        this.ethConfig = ethConfig;
    }

    @PostConstruct
    public void run() throws Exception {
        log.info("SCHEDLR watching WorkerPoolSubscriptionEvent");
        iexecHubService.getIexecHub().workerPoolSubscriptionEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(this::onSubscription);
    }


    @Override
    public void onSubscription(IexecHub.WorkerPoolSubscriptionEventResponse workerPoolSubscriptionEvent) {
        if (workerPoolSubscriptionEvent.workerPool.equals(workerPoolService.getWorkerPoolAddress())) {
            log.info("SCHEDLR received WorkerPoolSubscriptionEvent for worker " + workerPoolSubscriptionEvent.worker);
            workerSubscribed = true;
            //TODO - emitMarketOrder if n workers are alive (not subscribed, means nothing)
            try {
                Float deposit = (workerPoolService.getPoolConfig().getStakeRatioPolicy().floatValue() / 100) * mockConfig.getEmitMarketOrder().getValue().floatValue();//(30/100)*100
                BigInteger depositBig = BigDecimal.valueOf(deposit).toBigInteger();
                TransactionReceipt approveReceipt = rlcService.getRlc().approve(iexecHubService.getIexecHub().getContractAddress(), BigInteger.valueOf(100)).send();
                log.info("SCHEDLR approve (emitMarketOrder) " + 100 + " " + getStatus(approveReceipt));
                TransactionReceipt depositReceipt = iexecHubService.getIexecHub().deposit(depositBig).send();
                log.info("SCHEDLR deposit (emitMarketOrder) " + depositBig + " " + getStatus(depositReceipt));

                log.info(mockConfig.getEmitMarketOrder().getValue().toString());

                TransactionReceipt emitMarketOrderReceipt = marketplaceService.getMarketplace().emitMarketOrder(
                        mockConfig.getEmitMarketOrder().getDirection(),
                        mockConfig.getEmitMarketOrder().getCategory(),
                        mockConfig.getEmitMarketOrder().getTrust(),
                        mockConfig.getEmitMarketOrder().getValue(),
                        workerPoolService.getWorkerPoolAddress(),
                        mockConfig.getEmitMarketOrder().getVolume()
                ).send();
                log.info("SCHEDLR emitMarketOrder " + getStatus(emitMarketOrderReceipt));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isWorkerSubscribed() {
        return workerSubscribed;
    }
}
