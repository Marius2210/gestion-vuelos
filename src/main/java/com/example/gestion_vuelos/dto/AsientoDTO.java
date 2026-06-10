package com.example.gestion_vuelos.dto;

import lombok.Data;
import com.example.gestion_vuelos.model.AsientoEstado;

@Data
public class AsientoDTO {
    private Integer idAsientoVuelo;
    private String numeroAsiento;
    private Integer fila;
    private String letra;
    private String tipo;
    private AsientoEstado estado;
    private String reservaCodigo; // Si está ocupado, qué reserva lo ocupa
}
