package com.iexec.scheduler.ethereum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;


public class ConfigurationService {

    private static ConfigurationService instance;
    private String configPath;
    private Configuration configuration;


    public static ConfigurationService getInstance() {
        if (instance==null){
            instance = new ConfigurationService();
        }
        return instance;
    }

    private ConfigurationService() {
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

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public static void main(String[] args) {
        ConfigurationService.getInstance().setConfigPath("./iexec-scheduler/src/main/resources/application.yml");
        System.out.println(ConfigurationService.getInstance().getConfiguration());
    }

}
