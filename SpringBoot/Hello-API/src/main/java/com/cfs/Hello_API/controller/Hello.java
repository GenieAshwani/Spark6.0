package com.cfs.Hello_API.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello {

    @Autowired
    private Environment environment;


    @GetMapping("/message")
    public String message()
    {
        return environment.getRequiredProperty("app.message");
    }

    @GetMapping("/hello")
    public String helloMsg()
    {
        String port=environment.getProperty("server.port");
        return "Good Evening " + port;
    }

}
