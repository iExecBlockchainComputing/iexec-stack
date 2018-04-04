package com.iexec.worker.iexechub;

import com.iexec.worker.contracts.generated.IexecHub;
import com.iexec.worker.ethereum.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import static com.iexec.worker.ethereum.Utils.END;

public class IexecHubService {

    private static final Logger log = LoggerFactory.getLogger(IexecHubService.class);
    private static IexecHubService instance;
    private final Web3jService web3jService = Web3jService.getInstance();
    private final CredentialsService credentialsService = CredentialsService.getInstance();
    private final Configuration configuration = IexecConfigurationService.getInstance().getConfiguration();
    private final ContractConfig contractConfig = configuration.getContractConfig();
    private final Web3jConfig web3jConfig = configuration.getWeb3jConfig();
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
        this.iexecHub.workOrderCompletedEventObservable(web3jConfig.getStartBlockParameter(),END).subscribe(workOrderCompletedEvent -> {
            if (workOrderCompletedEvent.workerPool.equals(contractConfig.getWorkerPoolAddress()) && iexecHubWatcher != null){
                log.info("Received WorkOrderCompletedEvent [workOrderId:{}]", workOrderCompletedEvent.woid);
                iexecHubWatcher.onWorkOrderCompleted(workOrderCompletedEvent.woid);
            }
        });
        this.iexecHub.rewardEventObservable(web3jConfig.getStartBlockParameter(), END).subscribe(rewardEvent -> {
            if (rewardEvent.user.equals(credentialsService.getCredentials().getAddress())){
                log.info("Received RewardEvent [amount:{}]", rewardEvent.amount);
            }
        });
        this.iexecHub.seizeEventObservable(web3jConfig.getStartBlockParameter(), END).subscribe(seizeEvent -> {
            if (seizeEvent.user.equals(credentialsService.getCredentials().getAddress())){
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
