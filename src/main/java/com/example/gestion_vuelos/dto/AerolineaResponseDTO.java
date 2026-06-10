package com.example.gestion_vuelos.dto;

import lombok.Data;

@Data
public class AerolineaResponseDTO {
    private Integer idAerolinea;
    private String nombreAerolinea;
    private String codigoIata;
    private Integer cantidadAviones;
    private Integer cantidadTripulantes;
}
