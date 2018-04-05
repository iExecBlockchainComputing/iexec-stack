package com.iexec.worker.ethereum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.iexec.common.ethereum.CommonConfiguration;
import com.iexec.common.ethereum.IexecConfigurationService;
import com.iexec.common.ethereum.WalletConfig;

import java.io.File;


public class IexecWorkerLibrary {

    private static IexecWorkerLibrary instance;
    private String configPath;
    private WorkerConfiguration workerConfiguration;

    private IexecWorkerLibrary() {
    }

    private static IexecWorkerLibrary getInstance() {
        if (instance == null) {
            instance = new IexecWorkerLibrary();
        }
        return instance;
    }

    public static void initialize(String configurationFilePath, CommonConfigurationGetter commonConfigurationGetter) {
        if (configurationFilePath != null) {
            getInstance().setConfigPath(configurationFilePath);
            WorkerConfiguration workerConfiguration = IexecWorkerLibrary.getInstance().getWorkerConfiguration();
            IexecConfigurationService.initialize(workerConfiguration.getWalletConfig(), commonConfigurationGetter.getCommonConfiguration(workerConfiguration.getSchedulerUrl()));
        }
    }
    
    public static void initialize(WalletConfig walletConfig, CommonConfiguration commonConfiguration) {
        if (walletConfig != null && commonConfiguration != null) {
            IexecConfigurationService.initialize(walletConfig, commonConfiguration);
        }
    }

    private WorkerConfiguration getWorkerConfiguration() {
        if (this.workerConfiguration == null && configPath != null) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            try {
                this.workerConfiguration = mapper.readValue(new File(configPath), WorkerConfiguration.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.workerConfiguration;
    }

    private void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

}
