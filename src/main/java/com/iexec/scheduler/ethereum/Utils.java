package com.iexec.scheduler.ethereum;

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

public final class Utils {

    private Utils() {
        throw new IllegalAccessError("Utility class");
    }

    public static String signByteResult(String result, String address) {
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

}
