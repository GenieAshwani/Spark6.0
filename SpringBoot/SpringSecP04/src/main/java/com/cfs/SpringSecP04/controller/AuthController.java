package com.cfs.SpringSecP04.controller;

import com.cfs.SpringSecP04.entity.AppUser;
import com.cfs.SpringSecP04.entity.Role;
import com.cfs.SpringSecP04.repo.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserRepository repository;

    private final PasswordEncoder passwordEncoder;

    public AuthController(AppUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> register(@RequestBody RegisterRequest request )
    {
        String email=request.email.toLowerCase();

        if(repository.existsByEmail(email))
        {
            System.out.println("Email already Exists");
            throw  new IllegalArgumentException("Email already registered");
        }

        AppUser user= new AppUser();
        user.setName(request.name());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        repository.save(user);
        return Map.of("Message","Registration Successful","email",email);

    }


    public record RegisterRequest(
            String name,
            String email,
            String password
    )
    {

    }

}
