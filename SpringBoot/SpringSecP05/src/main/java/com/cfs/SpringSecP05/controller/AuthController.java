package com.cfs.SpringSecP05.controller;

import com.cfs.SpringSecP05.dto.LoginRequest;
import com.cfs.SpringSecP05.dto.TokenResponse;
import com.cfs.SpringSecP05.repo.AppUserRepository;
import com.cfs.SpringSecP05.service.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AppUserRepository repository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request)
    {
        Authentication authentication=authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.
                        unauthenticated(request.email().toLowerCase(),request.password()));
        return new TokenResponse(tokenService.create(authentication),
                "Bearer",tokenService.expireInSeconds());

    }
}
