package com.iexec.worker.actuator;

import com.iexec.common.contracts.generated.IexecHub;
import com.iexec.common.contracts.generated.WorkerPool;
import com.iexec.common.contracts.generated.WorkerPoolHub;
import com.iexec.common.ethereum.*;
import com.iexec.worker.iexechub.IexecHubService;
import com.iexec.worker.workerpool.WorkerPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Hash;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.List;

import static com.iexec.common.ethereum.Utils.*;


public class ActuatorService implements Actuator {

    private static final Logger log = LoggerFactory.getLogger(ActuatorService.class);
    private final static IexecHubService iexecHubService = IexecHubService.getInstance();
    private final static WorkerPoolService workerPoolService = WorkerPoolService.getInstance();
    private final static RlcService rlcService = RlcService.getInstance();
    private final static CredentialsService credentialsService = CredentialsService.getInstance();
    private static ActuatorService instance;
    private final Web3jService web3jService = Web3jService.getInstance();
    private final CommonConfiguration configuration = IexecConfigurationService.getInstance().getCommonConfiguration();
    private final ContractConfig contractConfig = configuration.getContractConfig();

    private ActuatorService() {
    }

    public static ActuatorService getInstance() {
        if (instance == null) {
            instance = new ActuatorService();
        }
        return instance;
    }

    @Override
    public TransactionStatus depositRlc(BigInteger rlcDepositRequested) {
        return Utils.depositRlc(rlcDepositRequested, rlcService.getRlc(), iexecHubService.getIexecHub(), log);
    }

    @Override
    public TransactionStatus depositRlc() {
        return depositRlc(IexecConfigurationService.getInstance().getWalletConfig().getRlcDeposit());
    }

    @Override
    public TransactionStatus subscribeToPool() {
        try {
            TransactionReceipt subscribeToPoolReceipt = workerPoolService.getWorkerPool().subscribeToPool().send();
            List<IexecHub.WorkerPoolSubscriptionEventResponse> workerPoolSubscriptionEvents = iexecHubService.getIexecHub().getWorkerPoolSubscriptionEvents(subscribeToPoolReceipt);
            log.info("SubscribeToPool [transactionHash:{}, transactionStatus:{}] ", subscribeToPoolReceipt.getTransactionHash(), getTransactionStatusFromEvents(workerPoolSubscriptionEvents));

            return getTransactionStatusFromEvents(workerPoolSubscriptionEvents);//return full event ? workerPoolSubscriptionEvents.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }

    @Override
    public TransactionStatus unsubscribeFromPool() {
        try {
            String workerPoolHubAddress = IexecHubService.getInstance().getIexecHub().workerPoolHub().send();
            WorkerPoolHub workerPoolHub = WorkerPoolHub.load(
                    workerPoolHubAddress, web3jService.getWeb3j(), credentialsService.getCredentials(), configuration.getNodeConfig().getGasPrice(), configuration.getNodeConfig().getGasLimit());
            String workerAffectation = workerPoolHub.getWorkerAffectation(credentialsService.getCredentials().getAddress()).send();
            WorkerPool workerPool = WorkerPool.load(
                    workerAffectation, web3jService.getWeb3j(), credentialsService.getCredentials(), configuration.getNodeConfig().getGasPrice(), configuration.getNodeConfig().getGasLimit());
            TransactionReceipt unsubscribeFromPoolReceipt = workerPool.unsubscribeFromPool().send();
            List<WorkerPool.WorkerUnsubscribeEventResponse> workerUnsubscribeEvents = workerPool.getWorkerUnsubscribeEvents(unsubscribeFromPoolReceipt);
            log.info("UnsubscribeFromPool [transactionHash:{}, transactionStatus:{}] ", unsubscribeFromPoolReceipt.getTransactionHash(), getTransactionStatusFromEvents(workerUnsubscribeEvents));
            return getTransactionStatusFromEvents(workerUnsubscribeEvents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }

    @Override
    public TransactionStatus contribute(String workOrderId, String workerResult, BigInteger contributeV, String contributeR, String contributeS) {
        String hashResult = hashResult(workerResult);
        String signResult = signByteResult(workerResult, credentialsService.getCredentials().getAddress());

        byte[] hashResultBytes = Numeric.hexStringToByteArray(hashResult);
        byte[] hashSignBytes = Numeric.hexStringToByteArray(signResult);
        byte[] r = Numeric.hexStringToByteArray(asciiToHex(contributeR));
        byte[] s = Numeric.hexStringToByteArray(asciiToHex(contributeS));

        try {
            TransactionReceipt contributeReceipt = workerPoolService.getWorkerPool().contribute(workOrderId, hashResultBytes, hashSignBytes, contributeV, r, s).send();
            List<WorkerPool.ContributeEventResponse> contributeEvents = workerPoolService.getWorkerPool().getContributeEvents(contributeReceipt);
            log.info("Contribute [workOrderId:{}, hashResult:{}, signResult:{}, transactionHash:{}, transactionStatus:{}]", workOrderId, hashResult, signResult, contributeReceipt.getTransactionHash(), getTransactionStatusFromEvents(contributeEvents));
            return getTransactionStatusFromEvents(contributeEvents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }

    @Override
    public TransactionStatus reveal(String workOrderId, String workerResult) {
        String shaResult = Hash.sha3String(workerResult);
        byte[] result = Numeric.hexStringToByteArray(shaResult);
        TransactionReceipt revealReceipt = null;
        try {
            revealReceipt = workerPoolService.getWorkerPool().reveal(workOrderId, result).send();
            List<WorkerPool.RevealEventResponse> revealEvents = workerPoolService.getWorkerPool().getRevealEvents(revealReceipt);
            log.info("Reveal [workOrderId:{}, hashResult:{}, transactionHash:{}, transactionStatus:{}]", workOrderId, shaResult, revealReceipt.getTransactionHash(), getTransactionStatusFromEvents(revealEvents));
            return getTransactionStatusFromEvents(revealEvents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }


}