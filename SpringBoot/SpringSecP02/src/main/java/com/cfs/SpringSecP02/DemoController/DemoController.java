package com.cfs.SpringSecP02.DemoController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/public/hello")
    public Map<String,String> hello()
    {
        return Map.of("Message","Anyone can access this");
    }

    @GetMapping("/profile")
    public Map<String,String> profile(Principal principal)
    {
        return Map.of("loggedInUser",principal.getName());
    }



    @GetMapping("/admin/report")
    public Map<String,String> report(Principal principal)
    {
        return Map.of("report","only ADMIN can access");
    }
}
