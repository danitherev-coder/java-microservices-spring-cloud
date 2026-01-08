package com.microservice.auth.exceptions;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class ApiErrors extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final HttpStatus status;
    private final String message;

    public ApiErrors(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public HttpStatus getEstado() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
