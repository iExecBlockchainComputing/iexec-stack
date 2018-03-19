package com.iexec.worker.mock;

import com.iexec.worker.contracts.generated.WorkerPool;

public interface RevealConsensus {

    void onRevealConsensus(WorkerPool.RevealConsensusEventResponse revealConsensusEvent);
}
