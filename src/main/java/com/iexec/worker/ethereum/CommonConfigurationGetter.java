package com.iexec.worker.ethereum;

import com.iexec.common.ethereum.CommonConfiguration;

public interface CommonConfigurationGetter {

    CommonConfiguration getCommonConfiguration(String schedulerUrl);

}
