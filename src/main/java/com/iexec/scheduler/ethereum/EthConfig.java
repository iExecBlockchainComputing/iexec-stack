package com.iexec.scheduler.ethereum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.web3j.protocol.core.DefaultBlockParameter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Validated
@Configuration
@ConfigurationProperties(prefix = "ethereum")
public class EthConfig {

    @NotNull
    private BigInteger startBlock;
    @NotNull
    private String iexecHubAddress;
    @NotNull
    private String rlcAddress;

    EthConfig() {
    }

    public BigInteger getStartBlock() {
        return startBlock;
    }

    public void setStartBlock(BigInteger startBlock) {
        this.startBlock = startBlock;
    }

    public DefaultBlockParameter getStartBlockParameter() {
        return DefaultBlockParameter.valueOf(startBlock);
    }

    public String getIexecHubAddress() {
        return iexecHubAddress;
    }

    public void setIexecHubAddress(String iexecHubAddress) {
        this.iexecHubAddress = iexecHubAddress;
    }

    public String getRlcAddress() {
        return rlcAddress;
    }

    public void setRlcAddress(String rlcAddress) {
        this.rlcAddress = rlcAddress;
    }
}