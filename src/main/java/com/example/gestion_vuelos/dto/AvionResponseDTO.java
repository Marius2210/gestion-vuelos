package com.example.gestion_vuelos.dto;

import lombok.Data;

@Data
public class AvionResponseDTO {
    private Integer idAvion;
    private String modelo;
    private Integer capacidad;
    private Integer idAerolinea;
    private String aerolineaNombre;
    private String aerolineaCodigoIata;
    private Integer cantidadVuelos;
    private Integer cantidadVuelosActivos;
}
