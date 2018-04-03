package com.iexec.worker.workerpool;

import com.iexec.worker.contracts.generated.WorkerPool;
import com.iexec.worker.ethereum.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import static com.iexec.worker.ethereum.Utils.END;

public class WorkerPoolService {

    private static final Logger log = LoggerFactory.getLogger(WorkerPoolService.class);
    private static WorkerPoolService instance;
    private final Web3jService web3jService = Web3jService.getInstance();
    private final CredentialsService credentialsService = CredentialsService.getInstance();
    private final Configuration configuration = IexecConfigurationService.getInstance().getConfiguration();
    private final Web3jConfig web3jConfig = configuration.getWeb3jConfig();
    private final ContractConfig contractConfig = configuration.getContractConfig();
    private WorkerPool workerPool;
    private WorkerPoolWatcher workerPoolWatcher;

    private WorkerPoolService() {
        run();
    }

    public static WorkerPoolService getInstance() {
        if (instance == null) {
            instance = new WorkerPoolService();
        }
        return instance;
    }

    public void run() {
        String workerPoolAddress = contractConfig.getWorkerPoolAddress();
        log.info("Loading WorkerPool contract [address:{}]", workerPoolAddress);
        if (workerPoolAddress != null) {
            workerPool = WorkerPool.load(
                    workerPoolAddress, web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

        }
        this.getWorkerPool().revealConsensusEventObservable(web3jConfig.getStartBlockParameter(), END)
                .subscribe(this::onRevealConsensus);
        this.getWorkerPool().allowWorkerToContributeEventObservable(web3jConfig.getStartBlockParameter(), END)
                .subscribe(this::onAllowWorkerToContribute);
    }

    private void onAllowWorkerToContribute(WorkerPool.AllowWorkerToContributeEventResponse allowWorkerToContributeEvent) {
        if (allowWorkerToContributeEvent.worker.equals(credentialsService.getCredentials().getAddress())) {
            log.info("Received AllowWorkerToContributeEvent [workOrderId:{}]", allowWorkerToContributeEvent.woid);
            workerPoolWatcher.onAllowWorkerToContribute(allowWorkerToContributeEvent.woid);
        }
    }

    private void onRevealConsensus(WorkerPool.RevealConsensusEventResponse revealConsensusEvent) {
        log.info("Received RevealConsensusEvent [workOrderId:{}]", revealConsensusEvent.woid);
        workerPoolWatcher.onRevealConsensus(revealConsensusEvent.woid);

    }

    public void registerWorkerPoolWatcher(WorkerPoolWatcher workerPoolWatcher) {
        this.workerPoolWatcher = workerPoolWatcher;
    }

    public WorkerPool getWorkerPool() {
        return workerPool;
    }

}
