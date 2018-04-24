package com.iexec.common.model;

import java.math.BigInteger;

public class WorkOrderModel {

    private BigInteger marketorderIdx;
    private String requester;
    private String app;
    private String dataset;
    private String workerpool;
    private BigInteger emitcost;
    private String params;
    private String callback;
    private String beneficiary;

    public WorkOrderModel(BigInteger marketorderIdx, String requester, String app, String dataset, String workerpool, BigInteger emitcost, String params, String callback, String beneficiary) {
        this.marketorderIdx = marketorderIdx;
        this.requester = requester;
        this.app = app;
        this.dataset = dataset;
        this.workerpool = workerpool;
        this.emitcost = emitcost;
        this.params = params;
        this.callback = callback;
        this.beneficiary = beneficiary;
    }

    public BigInteger getMarketorderIdx() {
        return marketorderIdx;
    }

    public void setMarketorderIdx(BigInteger marketorderIdx) {
        this.marketorderIdx = marketorderIdx;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
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

    public String getWorkerpool() {
        return workerpool;
    }

    public void setWorkerpool(String workerpool) {
        this.workerpool = workerpool;
    }

    public BigInteger getEmitcost() {
        return emitcost;
    }

    public void setEmitcost(BigInteger emitcost) {
        this.emitcost = emitcost;
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

    @Override
    public String toString() {
        return "WorkOrderModel{" +
                "marketorderIdx=" + marketorderIdx +
                ", requester='" + requester + '\'' +
                ", app='" + app + '\'' +
                ", dataset='" + dataset + '\'' +
                ", workerpool='" + workerpool + '\'' +
                ", emitcost=" + emitcost +
                ", params='" + params + '\'' +
                ", callback='" + callback + '\'' +
                ", beneficiary='" + beneficiary + '\'' +
                '}';
    }
}
