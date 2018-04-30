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
import xtremweb.common.*;
import xtremweb.database.SQLRequest;
import xtremweb.security.XWAccessRights;

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

    /**
     * This is a blockchain event watcher automatically called on worker subscription.
     * This retrieves a market order still starving computing resources,
     * and registers the worker as volunteer; if no such market order, this returns.
     * Finally, the market order is created on the blockchain if it has enough volunteers.
     * @param workerWalletAddr is the worker wallet address
     */
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
            if (host.canContribute()) {
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

    /**
     * This retrieves user from DB
     * @param userAddr
     * @return
     */
    private UserInterface getUser(final String userAddr) {
        if((userAddr == null) || (userAddr.length() < 1)) {
            return null;
        }
        try {
            return DBInterface.getInstance().selectOne(new UserInterface(),
                    SQLRequest.MAINTABLEALIAS + "." + UserInterface.Columns.LOGIN +
                            "='" + userAddr + "'");
        } catch(final IOException e) {
            return null;
        }

    }
    /**
     * This retrieves app from DB
     * @param appName
     * @return
     */
    private AppInterface getApp(final String appName) {
        if((appName == null) || (appName.length() < 1)) {
            return null;
        }
        try {
            return DBInterface.getInstance().selectOne(new AppInterface(),
                    SQLRequest.MAINTABLEALIAS + "." + AppInterface.Columns.NAME.toString() +
                            "='" + appName + "'");
        } catch(final IOException e) {
            return null;
        }

    }
    /**
     * This retrieves the app as defined from the AppModel.
     * If app does not exist in DB, a new app is created and inserted in DB
     * @param appModel is the model received from the blockchain
     * @return the found or created app; null on error
     */
    private AppInterface getApp(final AppModel appModel) {

        try {
            if(appModel == null) {
                return null;
            }
            final AppInterface existingApp = getApp(appModel.getName());
            if (existingApp != null) {
                return existingApp;
            }

            final AppInterface newApp = new AppInterface(new UID());
            final UserInterface appOwner = getUser(appModel.getOwner());
            if(appOwner == null) {
                logger.warn("newApp owner not found " + appModel.getOwner());
                return null;
            }

            newApp.setOwner(appOwner.getUID());
            newApp.setName(appModel.getName());
            newApp.setPrice(appModel.getPrice().longValue());
            newApp.setAccessRights(new XWAccessRights(XWAccessRights.USERALL.value() & XWAccessRights.STICKYBIT_INT));

            final String appParams = appModel.getParams();
            if(appParams != null) {
                try {
                    final String envvars = XWTools.jsonValueFromString(appModel.getParams(), "envvars");
                    newApp.setEnvVars(envvars);
                } catch (final Exception e) {
                    logger.warn("can't extract newApp envvars from " + appParams);
                }
                try {
                    final String appTypeStr = XWTools.jsonValueFromString(appModel.getParams(), "type");
                    final AppTypeEnum appType = AppTypeEnum.valueOf(appTypeStr);
                    newApp.setType(appType);
                } catch (final Exception e) {
                    logger.warn("can't extract newApp type from " + appParams);
                }
            }

            DBInterface.getInstance().addApp(appOwner, newApp);

            return newApp;

        } catch(final Exception e) {
            logger.exception(e);
            return null;
        }
    }

    /**
     * This retrieves a market order from DB
     * @param idx is the market order id
     * @return the market order; null on error, or if not found
     */
    private MarketOrderInterface getMarketOrder(final  long idx) {
        try {
            return DBInterface.getInstance().marketOrderByIdx(idx);
        } catch(final Exception e) {
            logger.exception(e);
            return null;
        }
    }

    /**
     * This creates a new WorkInterface in DB from the provided WorkOrderModel.
     * This new work has as many replicat as expected by the market order
     * @param model is the work order model
     */
    private void createWork(WorkOrderModel model) {

        final MarketOrderInterface marketOrder = getMarketOrder(model.getMarketorderIdx().longValue());
        if(marketOrder == null) {
            logger.error("createWork() : can't retrieve market order : "
                    + model.getMarketorderIdx().longValue());
            return;
        }
        if(marketOrder.getWorkerPoolAddr().compareTo(model.getWorkerpool()) != 0) {
            logger.error("createWork() : worker pool mismatch : "
                    + marketOrder.getWorkerPoolAddr()
                    + model.getWorkerpool());
            return;
        }

        final AppModel appModel = ModelService.getInstance().getAppModel(model.getApp());
        if(appModel == null) {
            logger.error("createWork() : can't retrieve app model "
                    + model.getApp());
            return;
        }
        final AppInterface app = getApp(appModel);
        if(app == null) {
            logger.error ("createWork() : can't add/retrieve app " + appModel.getName());
            return;
        }

        final UserInterface requester = getUser(model.getRequester());
        if (requester == null) {
            logger.error("createWork() : unkown requester " + model.getRequester());
            return;
        }

        try {
            final WorkInterface work = new WorkInterface();
            work.setUID(new UID());
            work.setMarketOrderIdx(model.getMarketorderIdx().longValue());
            work.setMarketOrderUid(marketOrder.getUID());
            work.setOwner(requester.getUID());
            work.setApplication(app.getUID());
            work.setDataset(model.getDataset());
            work.setBeneficiary(model.getBeneficiary());
            work.setWorkerPool(model.getWorkerpool());
            work.setEmitCost(model.getEmitcost().longValue());
            work.setCmdLine(model.getParams());
            work.setCallback(model.getCallback());
            work.setBeneficiary(model.getBeneficiary());
            work.setExpectedReplications(marketOrder.getExpectedWorkers());
            work.setCategoryId(marketOrder.getCategoryId());

            work.insert();

            return;

        } catch (final Exception e) {
            logger.exception(e);
            return;
        }
    }
    /**
     * This is a blockchain event watcher automatically called on market order sale.
     * This first retrieves from DB or creates a new application in DB.
     * Then this registers a new WorkInterface in DB.
     *
     * @param workOrderId is the blockchain work order id
     */
    @Override
    public void onWorkOrderActivated(String workOrderId) {
        final WorkOrderModel workOrderModel = ModelService.getInstance().getWorkOrderModel(workOrderId);
        if(workOrderModel == null) {
            logger.error("onWorkOrderActivated() : can't retrieve work model "
                    + ModelService.getInstance().getWorkOrderModel(workOrderId));
            return;
        }

        createWork(workOrderModel);

        //        DatasetModel datasetModel = ModelService.getInstance().getDatasetModel(workOrderModel.getDataset());
        //actuatorService.allowWorkersToContribute(workOrderId, Arrays.asList("0x70a1bebd73aef241154ea353d6c8c52d420d4f5b"), "O");
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
