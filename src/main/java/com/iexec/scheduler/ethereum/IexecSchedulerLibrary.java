package com.iexec.scheduler.ethereum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.iexec.common.ethereum.CommonConfiguration;
import com.iexec.common.ethereum.IexecConfigurationService;
import com.iexec.common.ethereum.WalletConfig;

import java.io.File;


public class IexecSchedulerLibrary {

    private static IexecSchedulerLibrary instance;
    private String configPath;
    private SchedulerConfiguration schedulerConfiguration;


    private IexecSchedulerLibrary() {
    }

    private static IexecSchedulerLibrary getInstance() {
        if (instance == null) {
            instance = new IexecSchedulerLibrary();
        }
        return instance;
    }

    public static void initialize(String configurationFilePath) {
        if (configurationFilePath != null) {
            getInstance().setConfigPath(configurationFilePath);
            SchedulerConfiguration schedulerConfiguration = IexecSchedulerLibrary.getInstance().getSchedulerConfiguration();
            IexecConfigurationService.initialize(schedulerConfiguration.getWalletConfig(), schedulerConfiguration.getCommonConfiguration());
        }
    }

    public static void initialize(WalletConfig walletConfig, CommonConfiguration commonConfiguration) {
        if (walletConfig != null && commonConfiguration != null) {
            IexecConfigurationService.initialize(walletConfig, commonConfiguration);
        }
    }

    private SchedulerConfiguration getSchedulerConfiguration() {
        if (this.schedulerConfiguration == null && configPath != null) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            try {
                this.schedulerConfiguration = mapper.readValue(new File(configPath), SchedulerConfiguration.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.schedulerConfiguration;
    }

    private void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

}
