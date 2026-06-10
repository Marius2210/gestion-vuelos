package com.example.gestion_vuelos.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VueloResponseDTO {
    private Integer idVuelo;
    private String numeroVuelo;
    private String origen;
    private String destino;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegada;
    private String estado;
    private String estadoDescripcion;
    private Integer idAvion;
    private String avionModelo;
    private Integer capacidad;
    private String aerolineaNombre;
    private List<TarifaInfo> tarifas;
    private Integer cantidadReservas;
}
