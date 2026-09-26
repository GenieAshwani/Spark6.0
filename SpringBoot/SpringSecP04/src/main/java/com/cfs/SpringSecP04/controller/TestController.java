package com.cfs.SpringSecP04.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {


    @GetMapping("/profile")
    Map<String,String> profile(Principal principal)
    {
        System.out.println("user details "+ principal.getName());
        return Map.of("loggedInUser", principal.getName(),"authentication","Http Session");
    }

    @GetMapping("/admin/report")
    Map<String,String> report(Principal principal)
    {
        return Map.of("report","access by admin");
    }
}
