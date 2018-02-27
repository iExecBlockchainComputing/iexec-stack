package com.iexec.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
public class MockController {

    private MockWatcherService mockService;

    @Autowired
    public MockController(MockWatcherService mockService) {
        this.mockService = mockService;
    }

    @RequestMapping("/isalive")
    public boolean isAlive() throws Exception {
        return true;
    }


}