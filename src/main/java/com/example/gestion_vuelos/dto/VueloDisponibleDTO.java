package com.example.gestion_vuelos.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VueloDisponibleDTO {
    private Integer idVuelo;
    private String numeroVuelo;
    private String aerolineaNombre;
    private String origen;
    private String destino;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegada;
    private String avionModelo;
    private Integer capacidad;
    private List<TarifaInfo> tarifas;
}
