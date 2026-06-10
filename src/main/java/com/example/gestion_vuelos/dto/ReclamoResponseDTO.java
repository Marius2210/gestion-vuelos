package com.example.gestion_vuelos.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReclamoResponseDTO {
    private Integer idReclamo;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private String estadoReclamo;
    private ReservaInfoBasicaDTO reserva;
}
