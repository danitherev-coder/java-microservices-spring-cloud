package com.microservice.auth.service;

import com.microservice.auth.dto.AuthRequest;
import com.microservice.auth.dto.AuthResponse;

import java.util.Set;

public interface AuthService {

    AuthResponse saveUser(AuthRequest authRequest);
    
    String generateToken(String username);
    
    void addRolesToUser(String username, Set<String> roles);
    
    void validateToken(String token);
}
