package com.iexec.sample;

import com.iexec.scheduler.Application;
import org.junit.Test;

/**
 * Integration test to run our main application.
 */
public class GreeterContractIT {

    @Test
    public void testGreeterContract() throws Exception {
        Application.main(new String[]{ });
    }
}
