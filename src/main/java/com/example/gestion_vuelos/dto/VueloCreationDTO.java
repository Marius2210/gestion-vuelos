package com.example.gestion_vuelos.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VueloCreationDTO {
    @NotBlank(message = "El número de vuelo es obligatorio")
    @Pattern(regexp = "^[A-Z]{2,3}[0-9]{1,4}$", message = "Formato inválido. Ejemplo: AV402")
    private String numeroVuelo;

    @NotBlank(message = "El origen es obligatorio")
    private String origen;

    @NotBlank(message = "El destino es obligatorio")
    private String destino;

    @NotNull(message = "La fecha de salida es obligatoria")
    @Future(message = "La fecha de salida debe ser futura")
    private LocalDateTime fechaSalida;

    @NotNull(message = "La fecha de llegada es obligatoria")
    private LocalDateTime fechaLlegada;

    @NotNull(message = "El estado es obligatorio")
    @Pattern(regexp = "^[PVAC]$", message = "Estado inválido. Use: P, V, A, C")
    private String estado; // P, V, A, C

    @NotNull(message = "El ID del avión es obligatorio")
    private Integer idAvion;

    @NotNull(message = "Las tarifas son obligatorias")
    @Size(min = 1, message = "Debe tener al menos una tarifa")
    private List<TarifaCreationDTO> tarifas;

    @Data
    public static class TarifaCreationDTO {
        @NotBlank(message = "La clase de tarifa es obligatoria")
        private String clase;

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio mínimo es 0.01")
        @DecimalMax(value = "99999.99", message = "El precio máximo es 99999.99")
        private BigDecimal precio;
    }
}
