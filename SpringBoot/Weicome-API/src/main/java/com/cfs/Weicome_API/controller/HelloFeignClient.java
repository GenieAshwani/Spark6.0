package com.cfs.Weicome_API.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "HELLO-API")
public interface HelloFeignClient {

    //http://localhost:8081/

    @GetMapping("/hello")
    public String invokeHelloApi();
}
