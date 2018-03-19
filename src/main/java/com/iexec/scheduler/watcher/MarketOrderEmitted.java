package com.iexec.scheduler.watcher;

import com.iexec.scheduler.contracts.generated.Marketplace;

public interface MarketOrderEmitted {

    void onMarketOrderEmitted(Marketplace.MarketOrderEmittedEventResponse marketOrderEmittedEvent);
}
