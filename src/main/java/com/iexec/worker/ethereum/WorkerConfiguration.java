package com.iexec.worker.ethereum;

import com.iexec.common.ethereum.WalletConfig;

public class WorkerConfiguration {

    private WalletConfig walletConfig;
    private String schedulerUrl;

    public WalletConfig getWalletConfig() {
        return walletConfig;
    }

    public void setWalletConfig(WalletConfig walletConfig) {
        this.walletConfig = walletConfig;
    }

    public String getSchedulerUrl() {
        return schedulerUrl;
    }

    public void setSchedulerUrl(String schedulerUrl) {
        this.schedulerUrl = schedulerUrl;
    }
}