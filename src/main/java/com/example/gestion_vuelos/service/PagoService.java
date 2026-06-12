package com.example.gestion_vuelos.service;

import com.example.gestion_vuelos.dto.PagoRequest;
import com.example.gestion_vuelos.dto.PagoResponseDTO;
import com.example.gestion_vuelos.dto.ReservaInfoBasicaDTO;
import com.example.gestion_vuelos.model.*;
import com.example.gestion_vuelos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ReservaService reservaService;

    @Transactional
    public PagoResponseDTO confirmarPago(PagoRequest request) {
        Reserva reserva = reservaRepository.findById(request.getIdReserva())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (reserva.getPago() != null) {
            throw new RuntimeException("Esta reserva ya tiene un pago registrado");
        }

        // Validar monto
        if (request.getMonto().compareTo(reserva.getPrecioTotal()) != 0) {
            throw new RuntimeException("El monto no coincide con el precio total de la reserva");
        }

        Pago pago = new Pago();
        pago.setMonto(request.getMonto());
        pago.setMetodoPago(request.getMetodoPago());
        pago.setReserva(reserva);
        pago.setFechaPago(LocalDateTime.now());

        /*reserva.setEstadoReserva("CONF");
        reservaRepository.save(reserva);*/

        // Confirmar la reserva y ocupar asiento definitivamente
        reservaService.confirmarReserva(reserva);

        Pago savedPago = pagoRepository.save(pago);

        // Convertir a DTO
        return convertToResponseDTO(savedPago);
    }

    // Metodo adicional para obtener un pago por ID
    public PagoResponseDTO obtenerPago(Integer idPago) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        return convertToResponseDTO(pago);
    }

    // Metodo para obtener pago por reserva
    public PagoResponseDTO obtenerPagoPorReserva(Integer idReserva) {
        Pago pago = pagoRepository.findByReservaIdReserva(idReserva)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado para esta reserva"));
        return convertToResponseDTO(pago);
    }

    private PagoResponseDTO convertToResponseDTO(Pago pago) {
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setIdPago(pago.getIdPago());
        dto.setMonto(pago.getMonto());
        dto.setMetodoPago(pago.getMetodoPago());
        dto.setFechaPago(pago.getFechaPago());

        // Información básica de la reserva
        ReservaInfoBasicaDTO reservaInfo = new ReservaInfoBasicaDTO();
        reservaInfo.setIdReserva(pago.getReserva().getIdReserva());
        reservaInfo.setCodigoReserva(pago.getReserva().getCodigoReserva());
        reservaInfo.setEstadoReserva(pago.getReserva().getEstadoReserva());
        reservaInfo.setPasajeroNombre(pago.getReserva().getPasajero().getNombreCompleto());
        reservaInfo.setVueloInfo(
                pago.getReserva().getVuelo().getNumeroVuelo() + " - " +
                        pago.getReserva().getVuelo().getOrigen() + " a " +
                        pago.getReserva().getVuelo().getDestino()
        );

        dto.setReserva(reservaInfo);
        return dto;
    }
}
