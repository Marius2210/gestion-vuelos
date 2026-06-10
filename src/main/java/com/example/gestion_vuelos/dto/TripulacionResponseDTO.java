package com.example.gestion_vuelos.dto;

import lombok.Data;

@Data
public class TripulacionResponseDTO {
    private Integer idTripulante;
    private String nombre;
    private String cargo;
    private Integer idAerolinea;
    private String aerolineaNombre;
    private String aerolineaCodigoIata;
    private Integer cantidadVuelosAsignados;
}
