package com.iexec.scheduler.sandbox;

import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Sandbox {

    public static void predicNextAddress(Web3j web3j) throws Exception {
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
    }

}
