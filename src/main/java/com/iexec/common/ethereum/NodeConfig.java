package com.iexec.common.ethereum;

import org.web3j.protocol.core.DefaultBlockParameter;

import java.math.BigInteger;

public class NodeConfig {

    private String clientAddress;
    private int startBlock;

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public int getStartBlock() {
        return startBlock;
    }

    public void setStartBlock(int startBlock) {
        this.startBlock = startBlock;
    }

    public DefaultBlockParameter getStartBlockParameter() {
        return DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock));
    }

}
