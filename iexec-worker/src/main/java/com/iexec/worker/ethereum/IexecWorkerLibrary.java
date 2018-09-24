package com.iexec.worker.ethereum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.iexec.common.ethereum.CommonConfiguration;
import com.iexec.common.ethereum.IexecConfigurationService;
import com.iexec.common.ethereum.WalletConfig;

import java.io.File;


public class IexecWorkerLibrary {

    private static IexecWorkerLibrary instance;
    private Boolean rpcEnabled;
    private String configPath;
    private WorkerConfiguration workerConfiguration;

    private IexecWorkerLibrary() {
    }

    public static IexecWorkerLibrary getInstance() {
        if (instance == null) {
            instance = new IexecWorkerLibrary();
        }
        return instance;
    }

    public static void initialize(String configurationFilePath, CommonConfigurationGetter commonConfigurationGetter, boolean rpcEnabled) {
        IexecWorkerLibrary.getInstance().setRpcEnabled(rpcEnabled);
        if (configurationFilePath != null) {
            getInstance().setConfigPath(configurationFilePath);
            WorkerConfiguration workerConfiguration = IexecWorkerLibrary.getInstance().getWorkerConfiguration();
            if (workerConfiguration != null) {
                IexecConfigurationService.initialize(workerConfiguration.getWalletConfig(), commonConfigurationGetter.getCommonConfiguration(workerConfiguration.getSchedulerUrl()));
            } else {
                throw new ExceptionInInitializerError("Unable to initialize IexecWorkerLibrary (worker configuration file not found)");
            }
        }
    }

    public static void initialize(WalletConfig walletConfig, CommonConfiguration commonConfiguration, boolean rpcEnabled) {
        IexecWorkerLibrary.getInstance().setRpcEnabled(rpcEnabled);
        if (walletConfig != null && commonConfiguration != null) {
            IexecConfigurationService.initialize(walletConfig, commonConfiguration);
        } else {
            throw new ExceptionInInitializerError("Unable to initialize IexecWorkerLibrary (walletConfig or commonConfiguration is null)");
        }
    }

    public boolean getRpcEnabled() {
        return rpcEnabled;
    }

    private void setRpcEnabled(boolean rpcEnabled) {
        this.rpcEnabled = rpcEnabled;
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
