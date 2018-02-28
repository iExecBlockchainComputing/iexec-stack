package com.iexec.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.web3j.protocol.Web3j;

import javax.annotation.PostConstruct;

@Service
public class SchedulerApiService {

    private static final Logger log = LoggerFactory.getLogger(SchedulerApiService.class);

    @Value("${scheduler.api-url}")
    private String schedulerApiUrl;

    @Autowired
    public SchedulerApiService() {
    }

    public String getWorkerPool() {
        final String workerpoolUri = schedulerApiUrl + "/workerpool";
        return new RestTemplate().getForObject(workerpoolUri, String.class);
    }

    @PostConstruct
    public void run() throws Exception {
    }

}
