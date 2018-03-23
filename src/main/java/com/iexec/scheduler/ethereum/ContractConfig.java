package com.iexec.scheduler.ethereum;

import javax.validation.constraints.NotNull;

public class ContractConfig {

    @NotNull
    private String iexecHubAddress;
    @NotNull
    private String rlcAddress;

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
