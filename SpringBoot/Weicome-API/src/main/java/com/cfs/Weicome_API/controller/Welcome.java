package com.cfs.Weicome_API.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping
public class Welcome {

    @Autowired
    private Environment environment;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    HelloFeignClient helloFeignClient;

    @GetMapping("/welcome")
    public String welcome()
    {
        String welcome="Welcome to CFS ";
        String port=environment.getProperty("server.port");
        //ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8082/hello", String.class);
        //String body = response.getBody();

        String body = helloFeignClient.invokeHelloApi();
        return welcome + port +" "+ body;

    }
}
