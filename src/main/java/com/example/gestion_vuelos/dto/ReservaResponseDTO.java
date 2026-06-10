package com.example.gestion_vuelos.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class ReservaResponseDTO {
    private Integer idReserva;
    private String codigoReserva;
    private String estadoReserva;
    private LocalDateTime fechaReserva;
    private String asientoPreferencia;
    private BigDecimal precioTotal;
    private VueloInfoDTO vuelo;
    private PasajeroInfoDTO pasajero;
}
