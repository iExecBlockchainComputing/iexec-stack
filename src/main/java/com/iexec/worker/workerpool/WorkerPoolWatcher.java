package com.iexec.worker.workerpool;


public interface WorkerPoolWatcher {

    void onRevealConsensus(String workOrderId);

    void onCallForContribution(String workOrderId);

}
