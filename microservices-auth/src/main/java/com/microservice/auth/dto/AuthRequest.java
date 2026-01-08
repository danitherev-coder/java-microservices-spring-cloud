package com.microservice.auth.dto;

import jakarta.validation.constraints.NotNull;

public record AuthRequest(
        @NotNull(message = "No ingreso un nombre de usuario")
        String username,
        @NotNull(message = "No ingreso un password")
        String password
) {
}
