package com.iexec.scheduler.watcher;

import com.iexec.scheduler.contracts.generated.WorkerPool;

public interface Reveal {

    void onReveal(WorkerPool.RevealEventResponse revealEvent);
}
