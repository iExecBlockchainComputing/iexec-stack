package com.iexec.scheduler.mock;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@Validated
@Configuration
@ConfigurationProperties(prefix = "mock")
public class MockConfig {

    @NotNull
    private String workerResult;
    @NotNull
    private EmitMarketOrder emitMarketOrder;
    @NotNull
    private AnswerEmitWorkOrder answerEmitWorkOrder;
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

    public EmitMarketOrder getEmitMarketOrder() {
        return emitMarketOrder;
    }

    public void setEmitMarketOrder(EmitMarketOrder emitMarketOrder) {
        this.emitMarketOrder = emitMarketOrder;
    }

    public AnswerEmitWorkOrder getAnswerEmitWorkOrder() {
        return answerEmitWorkOrder;
    }

    public void setAnswerEmitWorkOrder(AnswerEmitWorkOrder answerEmitWorkOrder) {
        this.answerEmitWorkOrder = answerEmitWorkOrder;
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

    public static class EmitMarketOrder {

        private BigInteger direction;
        private BigInteger category;
        private BigInteger trust;
        private BigInteger value;
        private BigInteger volume;

        public BigInteger getDirection() {
            return direction;
        }

        public void setDirection(BigInteger direction) {
            this.direction = direction;
        }

        public BigInteger getCategory() {
            return category;
        }

        public void setCategory(BigInteger category) {
            this.category = category;
        }

        public BigInteger getTrust() {
            return trust;
        }

        public void setTrust(BigInteger trust) {
            this.trust = trust;
        }

        public BigInteger getValue() {
            return value;
        }

        public void setValue(BigInteger value) {
            this.value = value;
        }

        public BigInteger getVolume() {
            return volume;
        }

        public void setVolume(BigInteger volume) {
            this.volume = volume;
        }
    }


    public static class AnswerEmitWorkOrder {

        private String marketorderIdx;
        private String app;
        private String dataset;
        private String params;
        private String callback;
        private String beneficiary;

        public String getMarketorderIdx() {
            return marketorderIdx;
        }

        public void setMarketorderIdx(String marketorderIdx) {
            this.marketorderIdx = marketorderIdx;
        }

        public String getApp() {
            return app;
        }

        public void setApp(String app) {
            this.app = app;
        }

        public String getDataset() {
            return dataset;
        }

        public void setDataset(String dataset) {
            this.dataset = dataset;
        }

        public String getParams() {
            return params;
        }

        public void setParams(String params) {
            this.params = params;
        }

        public String getCallback() {
            return callback;
        }

        public void setCallback(String callback) {
            this.callback = callback;
        }

        public String getBeneficiary() {
            return beneficiary;
        }

        public void setBeneficiary(String beneficiary) {
            this.beneficiary = beneficiary;
        }
    }

    public static class CallForContribution {

        private List<String> workers;
        private String enclaveChallenge;

        public List<String> getWorkers() {
            return workers;
        }

        public void setWorkers(List<String> workers) {
            this.workers = workers;
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