package com.cfs.SpringSecP03.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/public/hello")
    Map<String,String> publicUrl()
    {
        return Map.of("Message","public API");
    }

    @GetMapping("/profile")
    Map<String,String> profile(Principal principal)
    {
        return Map.of("loggedInUser", principal.getName(),"authentication","Http Session");
    }
}
