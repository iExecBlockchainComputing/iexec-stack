package com.iexec.worker.mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockController {

    @Autowired
    public MockController() {
    }

    @RequestMapping("/isalive")
    public boolean isAlive() throws Exception {
        return true;
    }

}