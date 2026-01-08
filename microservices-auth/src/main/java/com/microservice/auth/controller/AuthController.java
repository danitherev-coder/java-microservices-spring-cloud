package com.microservice.auth.controller;

import com.microservice.auth.dto.AuthRequest;
import com.microservice.auth.entity.UserCredentials;
import com.microservice.auth.exceptions.AuthenticationException;
import com.microservice.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String addNewUser(@RequestBody @Valid UserCredentials credentials) {
        return authService.saveUser(credentials);
    }

    @PostMapping("/token")
    public String getToken(@RequestBody @Valid AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
            );
            if (authentication.isAuthenticated()) {
                return authService.generateToken(authRequest.username());
            }
            throw new AuthenticationException("No se pudo autenticar al usuario", HttpStatus.UNAUTHORIZED);
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Credenciales inválidas: El usuario o la contraseña son incorrectos", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new AuthenticationException("Error durante la autenticación: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        try {
            authService.validateToken(token);
            return "Token válido";
        } catch (Exception e) {
            throw new AuthenticationException("Token inválido o expirado", HttpStatus.FORBIDDEN);
        }
    }
}
