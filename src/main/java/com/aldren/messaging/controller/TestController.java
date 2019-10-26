package com.aldren.messaging.controller;

import com.aldren.messaging.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1")
public class TestController {

    @Autowired
    private TestService test;

    @GetMapping("/test")
    public void test() throws ParseException {
        test.insertTestData();
    }

}
