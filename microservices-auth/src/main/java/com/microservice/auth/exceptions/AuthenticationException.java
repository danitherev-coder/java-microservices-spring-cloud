package com.microservice.auth.exceptions;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends ApiErrors {
    
    public AuthenticationException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
    
    public AuthenticationException(String message, HttpStatus status) {
        super(status, message);
    }
} 