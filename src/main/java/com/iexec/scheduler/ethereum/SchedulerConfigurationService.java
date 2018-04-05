package com.iexec.scheduler.ethereum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;


public class SchedulerConfigurationService {

    private static SchedulerConfigurationService instance;
    private String configPath;
    private SchedulerConfiguration schedulerConfiguration;


    public static SchedulerConfigurationService getInstance() {
        if (instance==null){
            instance = new SchedulerConfigurationService();
        }
        return instance;
    }

    public static void initialize(String configurationFilePath) {
        if (configurationFilePath!=null){
            getInstance().setConfigPath(configurationFilePath);
            getInstance().getSchedulerConfiguration();
        }
    }

    private SchedulerConfigurationService() {
    }

    public SchedulerConfiguration getSchedulerConfiguration() {
        if (this.schedulerConfiguration ==null && configPath!=null){
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
