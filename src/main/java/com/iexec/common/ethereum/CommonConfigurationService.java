package com.iexec.common.ethereum;

public class CommonConfigurationService {

    private static CommonConfigurationService instance;
    private CommonConfiguration configuration;

    private CommonConfigurationService() {
    }

    public static CommonConfigurationService getInstance() {
        if (instance == null) {
            instance = new CommonConfigurationService();
        }
        return instance;
    }

    public CommonConfiguration getConfiguration() {
        return this.configuration;
    }

    public void setConfiguration(CommonConfiguration configuration) {
        this.configuration = configuration;
    }


}
