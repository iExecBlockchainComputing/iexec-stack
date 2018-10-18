package com.iexec.common.ethereum;

import com.iexec.common.contracts.generated.IexecHub;
import com.iexec.common.contracts.generated.RLC;
import com.iexec.common.model.*;
import org.slf4j.Logger;
import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tuples.generated.Tuple8;
import org.web3j.tx.Contract;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

public final class Utils {

    public static final DefaultBlockParameterName END = DefaultBlockParameterName.LATEST;
    public static final byte[] EMPTY_BYTE = new byte[0];
    private static final String HEX_PREFIX = "0x";

    private Utils() {
        throw new IllegalAccessError("Utility class");
    }

    public static String hashResult(String result) {
        if (!result.isEmpty()) {
            return Hash.sha3(result);
        }
        return "";
    }

    public static String signByteResult(String result, String address) {
        result = getHexaStringWithPrefix(result);
        
        if (!result.isEmpty()) {
            String addressHash = Hash.sha3(address);
            String xor = HEX_PREFIX;
            for (int i = 2; i < 66; i++) {
                Integer temp = Integer.parseInt(String.valueOf(result.charAt(i)), 16) ^ Integer.parseInt(String.valueOf(addressHash.charAt(i)), 16);
                xor += Integer.toHexString(temp);
            }
            return Hash.sha3(xor);
        }
        return "";
    }

    private static String getHexaStringWithPrefix(String result) {
        if (!result.isEmpty()) {
            if (result.length() == 66) {
                return result;
            } else if (result.length() == 64) {
                return HEX_PREFIX + result;
            }
        }
        return "";
    }

    public static String web3Sha3(Web3j web3j, String preimage) throws IOException {
        return web3j.web3Sha3(preimage).send().getResult();
    }

    public static String asciiToHex(String asciiValue) {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }

        return hex.toString() + "".join("", Collections.nCopies(32 - (hex.length() / 2), "00"));
    }

    public static TransactionStatus isMined(TransactionReceipt transactionReceipt) {
        if (transactionReceipt.getGasUsed().compareTo(Contract.GAS_LIMIT) < 0) {
            return TransactionStatus.SUCCESS;
        }
        return TransactionStatus.FAILURE;
    }

    public static TransactionStatus getTransactionStatusFromEvents(List<?> events) {
        if (events != null && events.size() > 0) {
            return TransactionStatus.SUCCESS;
        }
        return TransactionStatus.FAILURE;
    }

    public static MarketOrderModel tuple2MarketOrderModel(Tuple8<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, String> marketOrder) {
        if (marketOrder != null) {
            return new MarketOrderModel(marketOrder.getValue1(), marketOrder.getValue2(), marketOrder.getValue3(), marketOrder.getValue4(), marketOrder.getValue5(), marketOrder.getValue6(), marketOrder.getValue7(), marketOrder.getValue8());
        }
        return null;
    }

    public static ConsensusModel tuple2ConsensusModel(Tuple8<BigInteger, BigInteger, byte[], BigInteger, BigInteger, BigInteger, BigInteger, String> consensusDetail) {
        if (consensusDetail != null) {
            return new ConsensusModel(consensusDetail.getValue1(), consensusDetail.getValue2(), consensusDetail.getValue3(), consensusDetail.getValue4(), consensusDetail.getValue5(), consensusDetail.getValue6(), consensusDetail.getValue7(), consensusDetail.getValue8());
        }
        return null;
    }

    public static ContributionModel tuple2ContributionModel(Tuple6<BigInteger, byte[], byte[], String, BigInteger, BigInteger> contribution) {
        if (contribution != null) {
            return new ContributionModel(ContributionStatusEnum.values()[contribution.getValue1().intValue()], contribution.getValue2(), contribution.getValue3(), contribution.getValue4(), contribution.getValue5(), contribution.getValue6());
        }
        return null;
    }

    public static StateHistoryModel tuple2StateHistoryModel(Tuple2<BigInteger, BigInteger> stateHistoryModel) {
        if (stateHistoryModel != null) {
            return new StateHistoryModel(stateHistoryModel.getValue1(), stateHistoryModel.getValue2());
        }
        return null;
    }

    public static TransactionStatus depositRlc(BigInteger rlcDepositRequested, RLC rlc, IexecHub iexecHub, Logger log) {
        try {
            Tuple2<BigInteger, BigInteger> lastStakeAndLocked = iexecHub.checkBalance(CredentialsService.getInstance().getCredentials().getAddress()).send();
            BigInteger lastStake = lastStakeAndLocked.getValue1();
            log.info("Get last RLC stake [stakeAmount:{}, rlcDepositRequested:{}, transactionStatus:{}] ",
                    lastStake, rlcDepositRequested, TransactionStatus.SUCCESS);
            if (lastStake.compareTo(rlcDepositRequested) < 0) {
                BigInteger rlcDeposit = rlcDepositRequested.subtract(lastStake);
                TransactionReceipt approveReceipt = rlc.approve(iexecHub.getContractAddress(), rlcDeposit).send();
                List<RLC.ApprovalEventResponse> approvalEvents = rlc.getApprovalEvents(approveReceipt);
                log.info("Approve RLC amount [approveAmount:{}, transactionStatus:{}] ",
                        rlcDeposit, getTransactionStatusFromEvents(approvalEvents));
                TransactionReceipt depositReceipt = iexecHub.deposit(rlcDeposit).send();
                List<IexecHub.DepositEventResponse> depositEvents = iexecHub.getDepositEvents(depositReceipt);
                log.info("Deposit RLC amount [depositAmount:{}, transactionStatus:{}] ",
                        rlcDeposit, getTransactionStatusFromEvents(depositEvents));
                Tuple2<BigInteger, BigInteger> currentStakeAndLocked = iexecHub.checkBalance(CredentialsService.getInstance().getCredentials().getAddress()).send();
                BigInteger currentStake = currentStakeAndLocked.getValue1();
                log.info("Get current RLC stake [stakeAmount:{}, rlcDepositRequested:{}, transactionStatus:{}] ",
                        currentStake, rlcDepositRequested, TransactionStatus.SUCCESS);
                if (currentStake.equals(rlcDepositRequested)) {
                    return TransactionStatus.SUCCESS;
                }
            } else {
                return TransactionStatus.SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransactionStatus.FAILURE;
    }

}
