package com.iexec.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "workerpool-policy")
public class WorkerPoolPolicyConfig {

    private List<String> list;
    private BigInteger mode;

    WorkerPoolPolicyConfig() {
        this.list = new ArrayList<>();
    }

    public List<String> getList() {
        return this.list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public BigInteger getMode() {
        return mode;
    }

    public void setMode(BigInteger mode) {
        this.mode = mode;
    }
}