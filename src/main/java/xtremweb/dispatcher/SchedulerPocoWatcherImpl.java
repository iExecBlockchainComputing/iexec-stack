package xtremweb.dispatcher;

import com.iexec.common.contracts.generated.WorkerPool;
import com.iexec.common.ethereum.IexecConfigurationService;
import com.iexec.common.ethereum.Web3jService;
import com.iexec.common.model.AppModel;
import com.iexec.common.model.ModelService;
import com.iexec.common.model.WorkOrderModel;
import com.iexec.scheduler.actuator.ActuatorService;
import com.iexec.scheduler.database.ContributionService;
import com.iexec.scheduler.iexechub.IexecHubService;
import com.iexec.scheduler.iexechub.IexecHubWatcher;
import com.iexec.scheduler.workerpool.WorkerPoolService;
import com.iexec.scheduler.workerpool.WorkerPoolWatcher;
import xtremweb.common.HostInterface;
import xtremweb.common.Logger;
import xtremweb.common.MarketOrderInterface;

import java.io.IOException;
import java.math.BigInteger;

public class SchedulerPocoWatcherImpl implements IexecHubWatcher, WorkerPoolWatcher {

    private final static IexecHubService iexecHubService = IexecHubService.getInstance();
    private final static WorkerPoolService workerPoolService = WorkerPoolService.getInstance();
    private final static ContributionService contributionService = ContributionService.getInstance();
    private final static ActuatorService actuatorService = ActuatorService.getInstance();
    private final Logger logger;


    public SchedulerPocoWatcherImpl() {
        logger = new Logger(this);
        logger.info(IexecConfigurationService.getInstance().getCommonConfiguration().getContractConfig().getWorkerPoolConfig().getAddress());
        try {
            logger.info(Web3jService.getInstance().getWeb3j().web3ClientVersion().send().getWeb3ClientVersion());
        } catch (IOException e) {
            e.printStackTrace();
        }
        iexecHubService.registerIexecHubWatcher(this);
        workerPoolService.registerWorkerPoolWatcher(this);
    }

    @Override
    public void onSubscription(String workerWalletAddr) {
        System.out.println("********* ugo in the subscription");
        try {
            final HostInterface host = DBInterface.getInstance().host(workerWalletAddr);
            if(host == null) {
                logger.warn("onSubscription(" + workerWalletAddr +") : host not found");
                return;
            }
            logger.debug("onSubscription(" + workerWalletAddr + ") : " + host.toXml());
            MarketOrderInterface marketOrder = DBInterface.getInstance().marketOrderUnsatisfied();
            if(marketOrder == null) {
                logger.info("onSubscription(" + workerWalletAddr +") : no unsatisfied market order");
            }
            marketOrder = DBInterface.getInstance().marketOrder();
            if(marketOrder == null) {
                host.update();
                logger.warn("onSubscription(" + workerWalletAddr +") : no market order");
                return;
            }
            if (host.wantToContribute()) {
                marketOrder.addWorker(host);
                host.update();
                marketOrder.update();
            }
            if(marketOrder.canStart()) {
               final BigInteger marketOrderIdx = actuatorService.createMarketOrder(BigInteger.valueOf(marketOrder.getCategoryId()),
                       BigInteger.valueOf(marketOrder.getTrust()),
                       BigInteger.valueOf(marketOrder.getPrice()),
                       BigInteger.valueOf(marketOrder.getVolume()));
               marketOrder.setMarketOrderIdx(marketOrderIdx.longValue());
               marketOrder.update();
            }
        } catch (final IOException e) {
            logger.exception(e);
        }
    }

    @Override
    public void onWorkOrderActivated(String workOrderId) {
        //actuatorService.allowWorkersToContribute(workOrderId, Arrays.asList("0x70a1bebd73aef241154ea353d6c8c52d420d4f5b"), "O");
        WorkOrderModel workOrderModel = ModelService.getInstance().getWorkOrderModel(workOrderId);
        AppModel appModel = ModelService.getInstance().getAppModel(workOrderModel.getApp());
//        DatasetModel datasetModel = ModelService.getInstance().getDatasetModel(workOrderModel.getDataset());
    }

    @Override
    public void onContributeEvent(WorkerPool.ContributeEventResponse contributeEventResponse) {
        //actuatorService.revealConsensus(contributeEventResponse.woid, Utils.hashResult("iExec the wanderer"));
    }

    @Override
    public void onReveal(WorkerPool.RevealEventResponse revealEventResponse) {
        //actuatorService.finalizeWork(revealEventResponse.woid,"aStdout", "aStderr", "anUri");
    }
}
