package com.iexec.scheduler.helper;

import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Utils {

    static String signByteResult(String result, String address) {
        String resultHash = Hash.sha3String(result);
        String addressHash = Hash.sha3(address);
        String xor = "0x";
        for (int i = 2; i < 66; i++) {
            Integer temp = Integer.parseInt(String.valueOf(resultHash.charAt(i)), 16) ^ Integer.parseInt(String.valueOf(addressHash.charAt(i)), 16);
            xor += Integer.toHexString(temp);
        }
        String sign = Hash.sha3(xor);
        return sign;
    }

    public static String hashResult(String result) {
        return Hash.sha3(Hash.sha3String(result));
    }

    static String web3Sha3(Web3j web3j, String preimage) throws IOException {
        return web3j.web3Sha3(preimage).send().getResult();
    }

    static String asciiToHex(String asciiValue) {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }

        return hex.toString() + "".join("", Collections.nCopies(32 - (hex.length() / 2), "00"));
    }

    private void predicNextAddress(Web3j web3j) throws Exception {
        //address = sha3(rlp_encode(creator_account, creator_account_nonce))[12:]
        //web3.eth.getTransactionCount(accountAddress)

        String address = "0x8bd535d49b095ef648cd85ea827867d358872809";

        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                address, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        nonce.add(BigInteger.ONE);

        System.out.println(nonce.toString());


        List<RlpType> values = new ArrayList<>();
        values.add(RlpString.create(Numeric.hexStringToByteArray(address)));
        values.add(RlpString.create(nonce));

        RlpList rlpList = new RlpList(values);

        byte[] sha3 = Hash.sha3(RlpEncoder.encode(rlpList));

        System.out.println(Numeric.toHexString(sha3));

        //Basically, the last 20 bytes of the sha3 of the list composed by the adderess and the nonce RLP encoded
        sha3 = Arrays.copyOfRange(sha3, 0, 20);


        System.out.println(Numeric.toHexString(sha3));

         /*


        iexecHubService.getIexecHub().createWorkerPoolEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                .subscribe(createWorkerPoolEvent -> {

                    //if (createWorkerPoolEvent.workerPoolOwner.equals(poolConfig.)) {
                    log.warn("SCHEDLR received CreateWorkerPoolEvent " + createWorkerPoolEvent.workerPoolName + ":" + createWorkerPoolEvent.workerPool);
                    //WorkerPool workerPool = loadWorkerPool(createWorkerPoolEvent.workerPool);
                    //setupWorkerPool(workerPool);
                    //}
                });

        TransactionReceipt receipt = iexecHubService.getIexecHub().createWorkerPool(poolConfig.getName(),
                poolConfig.getSubscriptionLockStakePolicy(),
                poolConfig.getSubscriptionMinimumStakePolicy(),
                poolConfig.getSubscriptionMinimumScorePolicy()).send();




        EthGetTransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(receipt.getTransactionHash()).send();






        if (transactionReceipt.getTransactionReceipt().isPresent()){
            for (Log tmp : transactionReceipt.getResult().getLogs()){
                log.info(receipt.getLogs().indexOf(tmp)+" "+tmp.getData());

            }
        }

        log.info(transactionReceipt.getTransactionReceipt().get().getLogsBloom());
        log.info(receipt.getGasUsed().toString());*/

    }



}
