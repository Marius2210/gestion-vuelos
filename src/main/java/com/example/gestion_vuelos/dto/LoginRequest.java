package com.example.gestion_vuelos.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class LoginRequest {
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
