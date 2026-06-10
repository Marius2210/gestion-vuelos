package com.example.gestion_vuelos.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VueloInfoDTO {
    private Integer idVuelo;
    private String numeroVuelo;
    private String origen;
    private String destino;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegada;
    private String aerolineaNombre;
}