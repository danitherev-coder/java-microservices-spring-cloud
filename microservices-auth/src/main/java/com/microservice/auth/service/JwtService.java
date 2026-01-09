package com.microservice.auth.service;

import java.util.Set;

public interface JwtService {

    void validateToken(final String token);
    String generateToken(String username, Set<String> roles);
}
