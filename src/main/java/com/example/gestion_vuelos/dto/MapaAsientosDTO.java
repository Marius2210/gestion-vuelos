package com.example.gestion_vuelos.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class MapaAsientosDTO {
    private Integer idVuelo;
    private String numeroVuelo;
    private String origen;
    private String destino;
    private Integer capacidad;
    private Integer asientosDisponibles;
    private Integer asientosOcupados;
    private Map<Integer, List<AsientoDTO>> asientosPorFila;
}
