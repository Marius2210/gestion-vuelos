package com.example.gestion_vuelos.service;

import com.example.gestion_vuelos.dto.ReclamoRequest;
import com.example.gestion_vuelos.dto.ReclamoResponseDTO;
import com.example.gestion_vuelos.dto.ReservaInfoBasicaDTO;
import com.example.gestion_vuelos.model.*;
import com.example.gestion_vuelos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReclamoService {

    @Autowired
    private ReclamoRepository reclamoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    public ReclamoResponseDTO crearReclamo(ReclamoRequest request) {
        Reserva reserva = reservaRepository.findById(request.getIdReserva())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        Reclamo reclamo = new Reclamo();
        reclamo.setDescripcion(request.getDescripcion());
        reclamo.setReserva(reserva);
        reclamo.setEstadoReclamo("Abierto");
        reclamo.setFechaCreacion(LocalDateTime.now());

        Reclamo savedReclamo = reclamoRepository.save(reclamo);

        // Convertir a DTO
        return convertToResponseDTO(savedReclamo);
    }

    // Metodo adicional para obtener un reclamo por ID
    public ReclamoResponseDTO obtenerReclamo(Integer idReclamo) {
        Reclamo reclamo = reclamoRepository.findById(idReclamo)
                .orElseThrow(() -> new RuntimeException("Reclamo no encontrado"));
        return convertToResponseDTO(reclamo);
    }

    // Metodo para obtener todos los reclamos de una reserva
    public List<ReclamoResponseDTO> obtenerReclamosPorReserva(Integer idReserva) {
        List<Reclamo> reclamos = reclamoRepository.findByReservaIdReserva(idReserva);
        return reclamos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private ReclamoResponseDTO convertToResponseDTO(Reclamo reclamo) {
        ReclamoResponseDTO dto = new ReclamoResponseDTO();
        dto.setIdReclamo(reclamo.getIdReclamo());
        dto.setDescripcion(reclamo.getDescripcion());
        dto.setFechaCreacion(reclamo.getFechaCreacion());
        dto.setEstadoReclamo(reclamo.getEstadoReclamo());

        // Información básica de la reserva
        ReservaInfoBasicaDTO reservaInfo = new ReservaInfoBasicaDTO();
        reservaInfo.setIdReserva(reclamo.getReserva().getIdReserva());
        reservaInfo.setCodigoReserva(reclamo.getReserva().getCodigoReserva());
        reservaInfo.setEstadoReserva(reclamo.getReserva().getEstadoReserva());
        reservaInfo.setPasajeroNombre(reclamo.getReserva().getPasajero().getNombreCompleto());
        reservaInfo.setVueloInfo(
                reclamo.getReserva().getVuelo().getNumeroVuelo() + " - " +
                        reclamo.getReserva().getVuelo().getOrigen() + " a " +
                        reclamo.getReserva().getVuelo().getDestino()
        );

        dto.setReserva(reservaInfo);
        return dto;
    }
}
