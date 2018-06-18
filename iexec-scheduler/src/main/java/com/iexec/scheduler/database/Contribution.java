package com.iexec.scheduler.database;

public class Contribution {

    private String workOrderId;
    private String worker;
    private byte[] resultHash;

    public Contribution(String workOrderId, String worker, byte[] resultHash) {
        this.workOrderId = workOrderId;
        this.worker = worker;
        this.resultHash = resultHash;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public byte[] getResultHash() {
        return resultHash;
    }

    public void setResultHash(byte[] resultHash) {
        this.resultHash = resultHash;
    }
}
