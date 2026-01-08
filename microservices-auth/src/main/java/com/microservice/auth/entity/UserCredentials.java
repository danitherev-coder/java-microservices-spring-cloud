package com.microservice.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Debe ingresar el username")
    private String name;
    @Email(message = "El email ingresado no tiene un formato valido")
    @NotNull(message = "Debe ingresar un email")
    private String email;
    @NotNull(message = "El password debe ser ingresado")
    private String password;
}
