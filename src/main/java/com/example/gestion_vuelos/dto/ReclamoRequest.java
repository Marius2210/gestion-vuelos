package com.example.gestion_vuelos.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class ReclamoRequest {
    @NotNull(message = "ID de reserva obligatorio")
    private Integer idReserva;

    @NotBlank(message = "Descripción obligatoria")
    private String descripcion;
}
