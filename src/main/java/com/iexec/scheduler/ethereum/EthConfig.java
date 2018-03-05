package com.iexec.scheduler.ethereum;

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

}