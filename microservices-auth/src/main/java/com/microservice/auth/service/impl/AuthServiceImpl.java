package com.microservice.auth.service.impl;

import com.microservice.auth.client.RoleClient;
import com.microservice.auth.client.dto.RoleDto;
import com.microservice.auth.dto.AuthRequest;
import com.microservice.auth.dto.AuthResponse;
import com.microservice.auth.entity.UserCredentials;
import com.microservice.auth.repository.UserCredentialsRepository;
import com.microservice.auth.service.AuthService;
import com.microservice.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserCredentialsRepository userCredentialsRepository;
    private final RoleClient roleClient;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse saveUser(AuthRequest authRequest) {
        // Validar que el usuario no exista
        if (userCredentialsRepository.findByEmail(authRequest.getEmail()).isPresent()) {
            log.warn("Intento de registro con email duplicado: {}", authRequest.getEmail());
            throw new RuntimeException("Usuario con email " + authRequest.getEmail() + " ya existe");
        }

        // Ignorar roles enviados por el cliente. Siempre asignar USER desde roles-service.
        Set<String> rolesAsignados = new HashSet<>();
        try {
            RoleDto userRole = roleClient.getRoleByName("USER");
            if (userRole != null) {
                rolesAsignados.add("USER");
                log.debug("Role USER obtenido desde roles-service y asignado por defecto");
            } else {
                rolesAsignados.add("USER"); // fallback local si no existe en roles-service
                log.warn("Role USER no encontrado en roles-service, usando fallback local");
            }
        } catch (Exception e) {
            rolesAsignados.add("USER");
            log.warn("Error consultando roles-service, asignando USER por fallback: {}", e.getMessage());
        }

        // Crear y guardar usuario con role USER
        UserCredentials credentials = new UserCredentials();
        credentials.setName(authRequest.getName());
        credentials.setEmail(authRequest.getEmail());
        credentials.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        credentials.setRoles(rolesAsignados); // Guardar SIN prefijo

        UserCredentials savedUser = userCredentialsRepository.save(credentials);
        log.info("Usuario registrado: {} con roles: {}", savedUser.getEmail(), rolesAsignados);

        // Generar JWT con roles sin prefijo
        String token = jwtService.generateToken(savedUser.getEmail(), rolesAsignados);

        return AuthResponse.builder()
                .token(token)
                .message("Usuario registrado exitosamente")
                .build();
    }

    @Override
    public String generateToken(String username) {
        var user = userCredentialsRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return jwtService.generateToken(username, user.getRoles());
    }

    @Override
    @Transactional
    public void addRolesToUser(String username, Set<String> roles) {
        UserCredentials user = userCredentialsRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Set<String> updated = new HashSet<>(user.getRoles());
        updated.addAll(roles);
        user.setRoles(updated);
        userCredentialsRepository.save(user);
    }

    @Override
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }
}
