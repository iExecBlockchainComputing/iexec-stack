package com.iexec.worker.scheduler;

import com.iexec.worker.sandbox.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public String getIexecHub() {
        final String uri = schedulerApiUrl + "/iexechub";
        return new RestTemplate().getForObject(uri, String.class);
    }

    public String getRlc() {
        final String uri = schedulerApiUrl + "/rlc";
        return new RestTemplate().getForObject(uri, String.class);
    }

    public String getChallenge() {
        final String challengeUri = schedulerApiUrl + "/challenge";
        return new RestTemplate().getForObject(challengeUri, String.class);
    }

    public String postSignedChallenge(Signature signature) {
        final String signChallengeUri = schedulerApiUrl + "/signchallenge";
        return new RestTemplate().postForObject(signChallengeUri, signature, String.class);
    }

    @PostConstruct
    public void run() throws Exception {
    }

}
