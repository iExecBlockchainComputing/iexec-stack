package com.iexec.worker.actuator;

import com.iexec.common.contracts.generated.Marketplace;
import com.iexec.common.contracts.generated.WorkOrder;
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
            log.info("Approve for subscribeToPool [approveAmount:{}, transactionStatus:{}] ",
                    depositAmount, getStatus(approveReceipt));
            TransactionReceipt depositReceipt = iexecHubService.getIexecHub().deposit(depositAmount).send();
            log.info("Deposit for subscribeToPool [depositAmount:{}, transactionStatus:{}] ",
                    depositAmount, getStatus(depositReceipt));
            TransactionReceipt subscribeToPoolReceipt = workerPoolService.getWorkerPool().subscribeToPool().send();
            log.info("SubscribeToPool [transactionStatus:{}] ", getStatus(subscribeToPoolReceipt));
            return getStatus(subscribeToPoolReceipt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }

    @Override
    public TransactionStatus unsubscribeFromPool() {
        try {
            TransactionReceipt unsubscribeFromPoolReceipt = workerPoolService.getWorkerPool().unsubscribeFromPool().send();
            log.info("UnsubscribeFromPool [transactionStatus:{}] ", getStatus(unsubscribeFromPoolReceipt));
            return getStatus(unsubscribeFromPoolReceipt);
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
            TransactionReceipt contributeDepositReceipt = iexecHubService.getIexecHub().deposit(depositBig).send();
            log.info("Deposit for contribute [depositAmount:{}, transactionStatus:{}] ",
                    depositBig, getStatus(contributeDepositReceipt));
            TransactionReceipt contributeReceipt = workerPoolService.getWorkerPool().contribute(workOrderId, hashResultBytes, hashSignBytes, contributeV, r, s).send();
            log.info("Contribute [hashResult:{}, signResult:{}, transactionStatus:{}]", hashResult, signResult, getStatus(contributeReceipt));
            return getStatus(contributeReceipt);
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
            log.info("Reveal [hashResult:{}, transactionStatus:{}]", shaResult, getStatus(revealReceipt));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }


}
