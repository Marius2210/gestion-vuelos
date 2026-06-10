package com.example.gestion_vuelos.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class AvionCreationDTO {
    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad mínima es 1")
    @Max(value = 1000, message = "La capacidad máxima es 1000")
    private Integer capacidad;

    @NotNull(message = "El ID de la aerolínea es obligatorio")
    private Integer idAerolinea;
}
