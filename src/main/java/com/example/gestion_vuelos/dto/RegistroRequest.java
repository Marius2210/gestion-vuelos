package com.example.gestion_vuelos.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Data
public class RegistroRequest {
    @NotBlank(message = "Nombre completo obligatorio")
    private String nombreCompleto;

    @NotBlank(message = "Número de pasaporte obligatorio")
    @Size(min = 6, max = 20)
    private String numPasaporte;

    @NotNull(message = "Fecha de nacimiento obligatoria")
    @Past(message = "La fecha debe ser anterior a hoy")
    private LocalDateTime fechaNac;

    @NotBlank(message = "Nacionalidad obligatoria")
    private String nacionalidad;

    @NotBlank(message = "Email obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "Teléfono obligatorio")
    @Pattern(regexp = "^[0-9]{8}$", message = "El teléfono debe tener exactamente 8 dígitos (ejemplo: 76546789)")
    private String numTelefono;

    @NotBlank(message = "Contraseña obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
}
