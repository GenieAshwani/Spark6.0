package com.cfs.SpringSecP04.repo;

import com.cfs.SpringSecP04.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser,Long> {


    Optional<AppUser> findByEmail(String  email);

    boolean existsByEmail(String email);
}
