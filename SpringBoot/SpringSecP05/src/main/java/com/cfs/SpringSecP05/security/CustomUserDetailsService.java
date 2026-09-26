package com.cfs.SpringSecP05.security;


import com.cfs.SpringSecP05.entity.AppUser;
import com.cfs.SpringSecP05.repo.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository repository;

    public CustomUserDetailsService(AppUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        System.out.println("User email : "+email);
        AppUser appuser = repository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        System.out.println(appuser);


        return User.withUsername(appuser.getEmail())
                .password(appuser.getPassword())
                .roles(appuser.getRole().name())
                .disabled(!appuser.isEnabled())
                .build();
    }

 
}
