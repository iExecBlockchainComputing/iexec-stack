package com.iexec.scheduler.ethereum;

import com.iexec.common.ethereum.CommonConfiguration;
import com.iexec.common.ethereum.IexecConfigurationService;
import com.iexec.common.ethereum.WalletConfig;

public class SchedulerConfiguration {

    private WalletConfig walletConfig;
    private CommonConfiguration commonConfiguration;

    /*
    public CommonConfiguration getCommonConfiguration() {
        return IexecConfigurationService.getInstance().getConfiguration();
    }

    public void setCommonConfiguration(CommonConfiguration commonConfiguration) {
        CommonIexecConfigurationService.getInstance().setConfiguration(commonConfiguration);
    }*/

    public CommonConfiguration getCommonConfiguration() {
        return commonConfiguration;
    }

    public void setCommonConfiguration(CommonConfiguration commonConfiguration) {
        this.commonConfiguration = commonConfiguration;
    }

    public WalletConfig getWalletConfig() {
        return walletConfig;
    }

    public void setWalletConfig(WalletConfig walletConfig) {
        this.walletConfig = walletConfig;
    }
}