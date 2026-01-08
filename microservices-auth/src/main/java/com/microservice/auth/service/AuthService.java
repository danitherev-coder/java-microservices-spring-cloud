package com.microservice.auth.service;

import com.microservice.auth.entity.UserCredentials;

public interface AuthService {

    String saveUser(UserCredentials credentials);
    String generateToken(String username);
    void validateToken(String token);
}
