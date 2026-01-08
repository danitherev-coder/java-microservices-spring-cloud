package com.microservice.gateway.microservice_gateway.util;

public interface JwtService {

    void validateToken(final String token);
}
