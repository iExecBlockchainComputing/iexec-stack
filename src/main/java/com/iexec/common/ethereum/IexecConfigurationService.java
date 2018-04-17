package com.iexec.common.ethereum;

public class IexecConfigurationService {

    private static IexecConfigurationService instance;
    private WalletConfig walletConfig;
    private CommonConfiguration commonConfiguration;

    private IexecConfigurationService() {
    }

    public static void initialize(WalletConfig walletConfig, CommonConfiguration commonConfiguration) {
        if (walletConfig != null && commonConfiguration != null) {
            getInstance().setWalletConfig(walletConfig);
            getInstance().setCommonConfiguration(commonConfiguration);
        } else {
            throw new ExceptionInInitializerError("Failed to initialize IexecConfigurationService (bad walletConfig or commonConfiguration)");
        }
    }

    public static IexecConfigurationService getInstance() {
        if (instance == null) {
            instance = new IexecConfigurationService();
        }
        return instance;
    }

    public WalletConfig getWalletConfig() {
        return walletConfig;
    }

    private void setWalletConfig(WalletConfig walletConfig) {
        this.walletConfig = walletConfig;
    }

    public CommonConfiguration getCommonConfiguration() {
        return this.commonConfiguration;
    }

    private void setCommonConfiguration(CommonConfiguration commonConfiguration) {
        this.commonConfiguration = commonConfiguration;
    }
}
