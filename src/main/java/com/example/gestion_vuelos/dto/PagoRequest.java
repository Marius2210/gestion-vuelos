package com.example.gestion_vuelos.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class PagoRequest {
    @NotNull(message = "ID de reserva obligatorio")
    private Integer idReserva;

    @NotNull(message = "Monto obligatorio")
    @DecimalMin(value = "0.01", message = "Monto mínimo 0.01")
    private BigDecimal monto;

    @NotBlank(message = "Método de pago obligatorio")
    private String metodoPago;
}
