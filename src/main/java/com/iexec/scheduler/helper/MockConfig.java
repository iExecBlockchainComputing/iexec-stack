package com.iexec.scheduler.helper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
@Configuration
@ConfigurationProperties(prefix = "mock")
public class MockConfig {

    @NotNull
    private String workerResult;
    @NotNull
    private CallForContribution callForContribution;
    @NotNull
    private FinalizeWork finalizeWork;

    MockConfig() {
    }

    public String getWorkerResult() {
        return workerResult;
    }

    public void setWorkerResult(String workerResult) {
        this.workerResult = workerResult;
    }

    public CallForContribution getCallForContribution() {
        return callForContribution;
    }

    public void setCallForContribution(CallForContribution callForContribution) {
        this.callForContribution = callForContribution;
    }

    public FinalizeWork getFinalizeWork() {
        return finalizeWork;
    }

    public void setFinalizeWork(FinalizeWork finalizeWork) {
        this.finalizeWork = finalizeWork;
    }


    public static class CallForContribution {

        private String worker;
        private String enclaveChallenge;

        public String getWorker() {
            return worker;
        }

        public void setWorker(String worker) {
            this.worker = worker;
        }

        public String getEnclaveChallenge() {
            return enclaveChallenge;
        }

        public void setEnclaveChallenge(String enclaveChallenge) {
            this.enclaveChallenge = enclaveChallenge;
        }
    }


    public static class FinalizeWork {

        private String stdout;
        private String stderr;
        private String uri;

        public String getStdout() {
            return stdout;
        }

        public void setStdout(String stdout) {
            this.stdout = stdout;
        }

        public String getStderr() {
            return stderr;
        }

        public void setStderr(String stderr) {
            this.stderr = stderr;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }

}