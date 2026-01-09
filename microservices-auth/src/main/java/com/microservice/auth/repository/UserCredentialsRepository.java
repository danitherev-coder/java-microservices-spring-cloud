package com.microservice.auth.repository;

import com.microservice.auth.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {
    Optional<UserCredentials> findByName(String username);

    Optional<UserCredentials> findByEmail(String email);
}

