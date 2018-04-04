package com.iexec.common.ethereum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;


public class IexecConfigurationService {

    private static IexecConfigurationService instance;
    private String configPath;
    private Configuration configuration;


    public static IexecConfigurationService getInstance() {
        if (instance==null){
            instance = new IexecConfigurationService();
        }
        return instance;
    }

    public static void initialize(String configurationFilePath) {
        if (configurationFilePath!=null){
            getInstance().setConfigPath(configurationFilePath);
        }
    }

    private IexecConfigurationService() {
    }

    public Configuration getConfiguration() {
        if (this.configuration==null && configPath!=null){
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            try {
                this.configuration = mapper.readValue(new File(configPath), Configuration.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.configuration;
    }

    private void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

}
