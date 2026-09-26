package com.cfs.SpringSecP05.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/profile")
    Map<String,String> profile(Principal principal)
    {
        System.out.println("user details "+ principal.getName());
        return Map.of("email", principal.getName(),"source","database");
    }

}
