package com.example.gestion_vuelos.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class BusquedaVueloRequest {
    @NotBlank(message = "Origen obligatorio")
    private String origen;

    @NotBlank(message = "Destino obligatorio")
    private String destino;

    @NotNull(message = "Fecha de salida obligatoria")
    private LocalDateTime fechaSalida;
}
