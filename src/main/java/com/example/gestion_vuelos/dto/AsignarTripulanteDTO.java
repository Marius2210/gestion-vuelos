package com.example.gestion_vuelos.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
public class AsignarTripulanteDTO {

    @NotNull(message = "El ID del vuelo es obligatorio")
    @Positive(message = "El ID del vuelo debe ser positivo")
    private Integer idVuelo;

    @NotNull(message = "Debe especificar al menos un tripulante")
    @Size(min = 1, message = "Debe asignar al menos un tripulante")
    private List<@NotNull @Positive Integer> idsTripulantes;
}
