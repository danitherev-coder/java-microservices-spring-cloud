package com.microservice.auth.service;

public interface JwtService {

    void validateToken(final String token);
    String generateToken(String username);
}
