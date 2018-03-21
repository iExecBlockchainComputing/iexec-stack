package com.iexec.scheduler.mock;

import com.iexec.scheduler.contracts.generated.Marketplace;
import com.iexec.scheduler.ethereum.EthConfig;
import com.iexec.scheduler.ethereum.RlcService;
import com.iexec.scheduler.iexechub.IexecHubService;
import com.iexec.scheduler.marketplace.MarketOrderDirectionEnum;
import com.iexec.scheduler.marketplace.MarketplaceService;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple7;

import javax.annotation.PostConstruct;
import java.math.BigInteger;

import static com.iexec.scheduler.ethereum.Utils.END;
import static com.iexec.scheduler.ethereum.Utils.getStatus;

@Service
public class MockCloudUserService {

    // /!\ ALL THIS BLOCK SHOULD MADE BY IEXECCLOUDUSER

    private static final Logger log = LoggerFactory.getLogger(MockCloudUserService.class);
    private final IexecHubService iexecHubService;
    private final WorkerPoolService workerPoolService;
    private final MarketplaceService marketplaceService;
    private final RlcService rlcService;
    private final MockConfig mockConfig;
    private final EthConfig ethConfig;

    @Autowired
    public MockCloudUserService(IexecHubService iexecHubService, WorkerPoolService workerPoolService,
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
        log.debug("CLDUSER watching marketOrderEmittedEvent (auto answerEmitWorkOrder)");
        marketplaceService.getMarketplace().marketOrderEmittedEventObservable(ethConfig.getStartBlockParameter(), END)
                .subscribe(this::onMarketOrderEmitted);
    }

    public void onMarketOrderEmitted(Marketplace.MarketOrderEmittedEventResponse marketOrderEmittedEvent) {
        log.debug("SCHEDLR received marketOrderEmittedEvent " + marketOrderEmittedEvent.marketorderIdx);
        answerEmitWorkOrder(marketOrderEmittedEvent);
    }

    private void answerEmitWorkOrder(Marketplace.MarketOrderEmittedEventResponse marketOrderEmittedEvent) {
        //populate map and expose
        try {
            BigInteger deposit = mockConfig.getEmitMarketOrder().getValue();
            log.debug(deposit.toString());
            TransactionReceipt approveReceipt = rlcService.getRlc().approve(iexecHubService.getIexecHub().getContractAddress(), deposit).send();
            log.debug("SCHEDLR approve (answerEmitWorkOrder) " + getStatus(approveReceipt));
            TransactionReceipt depositReceipt = iexecHubService.getIexecHub().deposit(deposit).send();
            log.debug("SCHEDLR deposit (answerEmitWorkOrder) " + getStatus(depositReceipt));

            Tuple7 orderBook = marketplaceService.getMarketplace().m_orderBook(marketOrderEmittedEvent.marketorderIdx).send();
            if (orderBook.getValue1().equals(MarketOrderDirectionEnum.ASK) &&
                    orderBook.getValue7().equals(workerPoolService.getPoolConfig().getAddress())) {
                TransactionReceipt answerEmitWorkOrderReceipt = iexecHubService.getIexecHub().answerEmitWorkOrder(marketOrderEmittedEvent.marketorderIdx,
                        workerPoolService.getPoolConfig().getAddress(),
                        mockConfig.getAnswerEmitWorkOrder().getApp(),
                        mockConfig.getAnswerEmitWorkOrder().getDataset(),
                        mockConfig.getAnswerEmitWorkOrder().getParams(),
                        mockConfig.getAnswerEmitWorkOrder().getCallback(),
                        mockConfig.getAnswerEmitWorkOrder().getBeneficiary()
                ).send();
                log.debug("SCHEDLR answerEmitWorkOrder " + getStatus(answerEmitWorkOrderReceipt));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
