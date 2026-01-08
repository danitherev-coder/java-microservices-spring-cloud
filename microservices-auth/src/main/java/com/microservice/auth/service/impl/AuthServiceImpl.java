package com.microservice.auth.service.impl;

import com.microservice.auth.entity.UserCredentials;
import com.microservice.auth.repository.UserCredentialsRepository;
import com.microservice.auth.service.AuthService;
import com.microservice.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserCredentialsRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;



    @Override
    public String saveUser(UserCredentials credentials) {
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        repository.save(credentials);
        return "user added to system";
    }

    @Override
    public String generateToken(String username) {
        return jwtService.generateToken(username);
    }

    @Override
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }
}
