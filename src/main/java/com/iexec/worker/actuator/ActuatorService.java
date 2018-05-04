package com.iexec.worker.actuator;

import com.iexec.common.contracts.generated.*;
import com.iexec.common.ethereum.*;
import com.iexec.common.workerpool.WorkerPoolConfig;
import com.iexec.worker.iexechub.IexecHubService;
import com.iexec.worker.workerpool.WorkerPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Hash;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
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
    private final NodeConfig nodeConfig = configuration.getNodeConfig();
    private final ContractConfig contractConfig = configuration.getContractConfig();
    private final WorkerPoolConfig workerPoolConfig = contractConfig.getWorkerPoolConfig();

    private ActuatorService() {
    }

    public static ActuatorService getInstance() {
        if (instance == null) {
            instance = new ActuatorService();
        }
        return instance;
    }

    @Override
    public TransactionStatus subscribeToPool() {
        try {
            BigInteger depositAmount = workerPoolConfig.getSubscriptionMinimumStakePolicy();
            TransactionReceipt approveReceipt = rlcService.getRlc().approve(iexecHubService.getIexecHub().getContractAddress(), depositAmount).send();
            List<RLC.ApprovalEventResponse> approvalEvents = rlcService.getRlc().getApprovalEvents(approveReceipt);
            log.info("Approve for subscribeToPool [approveAmount:{}, transactionStatus:{}] ",
                    depositAmount, getTransactionStatusFromEvents(approvalEvents));
            TransactionReceipt depositReceipt = iexecHubService.getIexecHub().deposit(depositAmount).send();
            List<IexecHub.DepositEventResponse> depositEvents = iexecHubService.getIexecHub().getDepositEvents(depositReceipt);
            log.info("Deposit for subscribeToPool [depositAmount:{}, transactionStatus:{}] ",
                    depositAmount, getTransactionStatusFromEvents(depositEvents));
            TransactionReceipt subscribeToPoolReceipt = workerPoolService.getWorkerPool().subscribeToPool().send();
            List<IexecHub.WorkerPoolSubscriptionEventResponse> workerPoolSubscriptionEvents = iexecHubService.getIexecHub().getWorkerPoolSubscriptionEvents(subscribeToPoolReceipt);
            log.info("SubscribeToPool [transactionStatus:{}] ", getTransactionStatusFromEvents(workerPoolSubscriptionEvents));

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
                    workerPoolHubAddress, web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
            String workerAffectation = workerPoolHub.getWorkerAffectation(credentialsService.getCredentials().getAddress()).send();
            WorkerPool workerPool = WorkerPool.load(
                    workerAffectation, web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
            TransactionReceipt unsubscribeFromPoolReceipt = workerPool.unsubscribeFromPool().send();
            List<WorkerPool.WorkerUnsubscribeEventResponse> workerUnsubscribeEvents = workerPool.getWorkerUnsubscribeEvents(unsubscribeFromPoolReceipt);
            log.info("UnsubscribeFromPool [transactionStatus:{}] ", getTransactionStatusFromEvents(workerUnsubscribeEvents));
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

        WorkOrder workOrder = WorkOrder.load(
                workOrderId, web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

        try {
            Marketplace marketplace = Marketplace.load(
                    iexecHubService.getIexecHub().marketplaceAddress().send(), web3jService.getWeb3j(), credentialsService.getCredentials(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
            BigInteger marketOrderValue = marketplace.getMarketOrderValue(workOrder.m_marketorderIdx().send()).send();
            log.info("[marketOrderValue:{}]", marketOrderValue);
            Float deposit = (workerPoolConfig.getStakeRatioPolicy().floatValue() / 100) * marketOrderValue.floatValue();//(30/100)*100
            BigInteger depositBig = BigDecimal.valueOf(deposit).toBigInteger();
            TransactionReceipt approveReceipt = rlcService.getRlc().approve(iexecHubService.getIexecHub().getContractAddress(), depositBig).send();
            List<RLC.ApprovalEventResponse> approvalEvents = rlcService.getRlc().getApprovalEvents(approveReceipt);
            log.info("Approve for contribute [approveAmount:{}, transactionStatus:{}] ",
                    depositBig, getTransactionStatusFromEvents(approvalEvents));
            TransactionReceipt contributeDepositReceipt = iexecHubService.getIexecHub().deposit(depositBig).send();
            List<IexecHub.DepositEventResponse> depositEvents = iexecHubService.getIexecHub().getDepositEvents(contributeDepositReceipt);
            log.info("Deposit for contribute [depositAmount:{}, transactionStatus:{}] ",
                    depositBig, getTransactionStatusFromEvents(depositEvents));
            TransactionReceipt contributeReceipt = workerPoolService.getWorkerPool().contribute(workOrderId, hashResultBytes, hashSignBytes, contributeV, r, s).send();
            List<WorkerPool.ContributeEventResponse> contributeEvents = workerPoolService.getWorkerPool().getContributeEvents(contributeReceipt);
            log.info("Contribute [hashResult:{}, signResult:{}, transactionStatus:{}]", hashResult, signResult, getTransactionStatusFromEvents(contributeEvents));
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
            log.info("Reveal [hashResult:{}, transactionStatus:{}]", shaResult, getTransactionStatusFromEvents(revealEvents));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }


}
