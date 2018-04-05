package com.iexec.worker.iexechub;

import com.iexec.common.contracts.generated.IexecHub;
import com.iexec.common.ethereum.*;
import com.iexec.common.workerpool.WorkerPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import static com.iexec.common.ethereum.Utils.END;


public class IexecHubService {

    private static final Logger log = LoggerFactory.getLogger(IexecHubService.class);
    private static IexecHubService instance;
    private final Web3jService web3jService = Web3jService.getInstance();
    private final CredentialsService credentialsService = CredentialsService.getInstance();
    private final CommonConfiguration configuration = IexecConfigurationService.getInstance().getCommonConfiguration();
    private final NodeConfig nodeConfig = configuration.getNodeConfig();
    private final ContractConfig contractConfig = configuration.getContractConfig();
    private final WorkerPoolConfig workerPoolConfig = contractConfig.getWorkerPoolConfig();
    private IexecHub iexecHub;
    private IexecHubWatcher iexecHubWatcher;

    private IexecHubService() {
        run();
    }

    public static IexecHubService getInstance() {
        if (instance == null) {
            instance = new IexecHubService();
        }
        return instance;
    }

    private void run() {
        this.iexecHub = IexecHub.load(
                contractConfig.getIexecHubAddress(), web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        this.iexecHub.workOrderCompletedEventObservable(nodeConfig.getStartBlockParameter(), END).subscribe(workOrderCompletedEvent -> {
            if (workOrderCompletedEvent.workerPool.equals(workerPoolConfig.getAddress()) && iexecHubWatcher != null) {
                log.info("Received WorkOrderCompletedEvent [workOrderId:{}]", workOrderCompletedEvent.woid);
                iexecHubWatcher.onWorkOrderCompleted(workOrderCompletedEvent.woid);
            }
        });
        this.iexecHub.rewardEventObservable(nodeConfig.getStartBlockParameter(), END).subscribe(rewardEvent -> {
            if (rewardEvent.user.equals(credentialsService.getCredentials().getAddress())) {
                log.info("Received RewardEvent [amount:{}]", rewardEvent.amount);
            }
        });
        this.iexecHub.seizeEventObservable(nodeConfig.getStartBlockParameter(), END).subscribe(seizeEvent -> {
            if (seizeEvent.user.equals(credentialsService.getCredentials().getAddress())) {
                log.info("Received SeizeEvent [amount:{}]", seizeEvent.amount);
            }
        });
    }

    public IexecHub getIexecHub() {
        return iexecHub;
    }

    public void registerIexecHubWatcher(IexecHubWatcher iexecHubWatcher) {
        this.iexecHubWatcher = iexecHubWatcher;
    }

}
