package com.example.gestion_vuelos.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class ReservaRequest {
    @NotNull(message = "ID de vuelo obligatorio")
    private Integer idVuelo;

    @NotNull(message = "ID del pasajero obligatorio")
    private Integer idPasajero;

    @NotBlank(message = "Asiento preferencia obligatorio")
    @Pattern(regexp = "^[0-9]{1,2}[A-F]$",
            message = "Formato de asiento inválido. Use: número(1-40) + letra(A-F). Ejemplo: 12A, 24B, 31F")
    private String asientoPreferencia;

    @NotNull(message = "ID de tarifa obligatorio")
    private Integer idTarifa;
}
