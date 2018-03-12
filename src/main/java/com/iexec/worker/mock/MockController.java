package com.iexec.worker.mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

//@RestController
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