package com.iexec.worker.workerpool;

import com.iexec.common.contracts.generated.WorkerPool;
import com.iexec.common.ethereum.*;
import com.iexec.common.model.ConsensusModel;
import com.iexec.common.model.ContributionModel;
import com.iexec.common.workerpool.WorkerPoolConfig;
import com.iexec.worker.ethereum.IexecWorkerLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.ens.EnsResolutionException;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import static com.iexec.common.ethereum.Utils.END;
import static com.iexec.common.ethereum.Utils.tuple2ConsensusModel;
import static com.iexec.common.ethereum.Utils.tuple2ContributionModel;


public class WorkerPoolService {

    private static final Logger log = LoggerFactory.getLogger(WorkerPoolService.class);
    private static WorkerPoolService instance;
    private final Web3jService web3jService = Web3jService.getInstance();
    private final CredentialsService credentialsService = CredentialsService.getInstance();
    private final CommonConfiguration configuration = IexecConfigurationService.getInstance().getCommonConfiguration();
    private final NodeConfig nodeConfig = configuration.getNodeConfig();
    private final ContractConfig contractConfig = configuration.getContractConfig();
    private final WorkerPoolConfig workerPoolConfig = contractConfig.getWorkerPoolConfig();
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

    private void run() {
        String workerPoolAddress = workerPoolConfig.getAddress();
        ExceptionInInitializerError exceptionInInitializerError = new ExceptionInInitializerError("Failed to load WorkerPool contract from address " + workerPoolAddress);
        if (workerPoolAddress != null && !workerPoolAddress.isEmpty()) {
            try {
                workerPool = WorkerPool.load(
                        workerPoolAddress, web3jService.getWeb3j(), credentialsService.getCredentials(), configuration.getNodeConfig().getGasPrice(), configuration.getNodeConfig().getGasLimit());
                //if (!workerPool.isValid()){ throw exceptionInInitializerError;}
                log.info("Loading WorkerPool contract [address:{}]", workerPoolAddress);
            } catch (EnsResolutionException e){
                throw exceptionInInitializerError;
            }
        } else {
            throw exceptionInInitializerError;
        }
        if (IexecWorkerLibrary.getInstance().getRpcEnabled()){
            this.getWorkerPool().revealConsensusEventObservable(nodeConfig.getStartBlockParameter(), END)
                    .subscribe(this::onRevealConsensus);
            this.getWorkerPool().allowWorkerToContributeEventObservable(nodeConfig.getStartBlockParameter(), END)
                    .subscribe(this::onAllowWorkerToContribute);
        }
    }

    private void onAllowWorkerToContribute(WorkerPool.AllowWorkerToContributeEventResponse allowWorkerToContributeEvent) {
        if (allowWorkerToContributeEvent.worker.equals(credentialsService.getCredentials().getAddress()) && workerPoolWatcher != null) {
            log.info("Received AllowWorkerToContributeEvent [workOrderId:{}]", allowWorkerToContributeEvent.woid);
            workerPoolWatcher.onAllowWorkerToContribute(allowWorkerToContributeEvent.woid);
        }
    }

    private void onRevealConsensus(WorkerPool.RevealConsensusEventResponse revealConsensusEvent) {
        if (workerPoolWatcher != null){
            log.info("Received RevealConsensusEvent [workOrderId:{}]", revealConsensusEvent.woid);
            workerPoolWatcher.onRevealConsensus(revealConsensusEvent.woid);
        }
    }

    public void registerWorkerPoolWatcher(WorkerPoolWatcher workerPoolWatcher) {
        this.workerPoolWatcher = workerPoolWatcher;
    }

    public WorkerPool getWorkerPool() {
        return workerPool;
    }

    public ContributionModel getWorkerContributionModelByWorkOrderId(String workOrderId) {
        ContributionModel contributionModel = null;
        TransactionStatus transactionStatus = TransactionStatus.SUCCESS;
        try {
            contributionModel = tuple2ContributionModel(workerPool.getContribution(workOrderId, CredentialsService.getInstance().getCredentials().getAddress()).send());
        } catch (Exception e) {
            transactionStatus = TransactionStatus.FAILURE;
        }
        log.info("GetContributionModel [workOrderId:{}, transactionStatus:{}] ",
               workOrderId, transactionStatus);
        return contributionModel;
    }

    public ConsensusModel getConsensusModelByWorkOrderId(String workOrderId) {
        ConsensusModel consensusModel = null;
        TransactionStatus transactionStatus = TransactionStatus.SUCCESS;
        try {
            consensusModel = tuple2ConsensusModel(workerPool.getConsensusDetails(workOrderId).send());
        } catch (Exception e) {
            transactionStatus = TransactionStatus.FAILURE;
        }
        log.info("GetConsensusModel [workOrderId:{}, transactionStatus:{}] ",
                workOrderId, transactionStatus);
        return consensusModel;
    }

}
