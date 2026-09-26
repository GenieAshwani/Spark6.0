package com.cfs.SpringSecP05;


import com.cfs.SpringSecP05.entity.AppUser;
import com.cfs.SpringSecP05.entity.Role;
import com.cfs.SpringSecP05.repo.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedUser(AppUserRepository repository, PasswordEncoder encoder)
    {
        return args -> {
            if(repository.count()>0) return;
            repository.save(user("Student","student@gmail.com","student123",Role.USER,encoder));
            repository.save(user("Admin","admin@gmail.com","admin123",Role.ADMIN,encoder));
        };
    }

    private AppUser user(String name, String email, String passWord, Role role, PasswordEncoder encoder)
    {
        AppUser user=new AppUser();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(encoder.encode(passWord));
        user.setRole(role);
        return user;
    }
}
