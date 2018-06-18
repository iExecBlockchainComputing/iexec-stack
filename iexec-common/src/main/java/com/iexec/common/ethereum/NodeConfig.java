package com.iexec.common.ethereum;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.web3j.protocol.core.DefaultBlockParameter;

import java.math.BigInteger;

public class NodeConfig {

    private String clientAddress;
    private BigInteger startBlock;
    private BigInteger gasPrice;
    private BigInteger gasLimit;


    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public BigInteger getStartBlock() {
        return startBlock;
    }

    public void setStartBlock(BigInteger startBlock) {
        this.startBlock = startBlock;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
    }

    @JsonIgnore
    public DefaultBlockParameter getStartBlockParameter() {
        return DefaultBlockParameter.valueOf(startBlock);
    }

}
