package com.microservice.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {
        @NotNull(message = "No ingreso un nombre de usuario")
        private String username;
        @NotNull(message = "No ingreso un password")
        private String password;
        private String name;
        private String email;
        private Set<String> roles;
}
