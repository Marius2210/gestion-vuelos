package com.example.gestion_vuelos.service;

import com.example.gestion_vuelos.dto.*;
import com.example.gestion_vuelos.model.*;
import com.example.gestion_vuelos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private PasajeroRepository pasajeroRepository;

    @Autowired
    private TarifaRepository tarifaRepository;

    @Autowired
    private AsientoService asientoService;

    @Autowired
    private AsientoVueloRepository asientoVueloRepository;

    @Transactional
    public ReservaResponseDTO crearReserva(ReservaRequest request) {
        Vuelo vuelo = vueloRepository.findById(request.getIdVuelo())
                .orElseThrow(() -> new RuntimeException("Vuelo no encontrado"));

        Pasajero pasajero = pasajeroRepository.findById(request.getIdPasajero())
                .orElseThrow(() -> new RuntimeException("Pasajero no encontrado"));

        Tarifa tarifa = tarifaRepository.findById(request.getIdTarifa())
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada"));

        // Verificar formato del asiento
        if (!asientoService.validarFormatoAsiento(request.getAsientoPreferencia(), vuelo.getAvion().getCapacidad())) {
            throw new RuntimeException("Formato de asiento inválido. Use: número(1-40) + letra(A-F). Ejemplo: 12A, 24B, 31F");
        }

        // Verificar que el asiento exista en este vuelo
        AsientoVuelo asiento = asientoVueloRepository.findByVueloAndNumeroAsiento(vuelo, request.getAsientoPreferencia())
                .orElseThrow(() -> new RuntimeException("El asiento " + request.getAsientoPreferencia() +
                        " no existe para este vuelo. Asientos disponibles: 1A hasta " +
                        (int)Math.ceil((double)vuelo.getAvion().getCapacidad() / 6) + "F"));

        // Verificar que el asiento esté disponible
        if (asiento.getEstado() != AsientoEstado.DISPONIBLE) {
            throw new RuntimeException("El asiento " + request.getAsientoPreferencia() +
                    " no está disponible. Estado actual: " + asiento.getEstado());
        }

        // Verificar que el mismo pasajero no tenga otra reserva activa en este vuelo
        boolean yaTieneReserva = reservaRepository.findAll().stream()
                .anyMatch(r -> r.getPasajero().getIdPasajero().equals(pasajero.getIdPasajero()) &&
                        r.getVuelo().getIdVuelo().equals(vuelo.getIdVuelo()) &&
                        !r.getEstadoReserva().equals("CANCELADA"));

        if (yaTieneReserva) {
            throw new RuntimeException("El pasajero ya tiene una reserva activa en este vuelo");
        }

        // Verificar disponibilidad de asientos en general
        long asientosDisponibles = asientoVueloRepository.countByVueloAndEstado(vuelo, AsientoEstado.DISPONIBLE);
        if (asientosDisponibles == 0) {
            throw new RuntimeException("No hay asientos disponibles en este vuelo");
        }

        /*// Verificar disponibilidad
        long reservasExistentes = reservaRepository.findAll().stream()
                .filter(r -> r.getVuelo().getIdVuelo().equals(vuelo.getIdVuelo()) &&
                        !r.getEstadoReserva().equals("CANCELADA"))
                .count();

        if (reservasExistentes >= vuelo.getAvion().getCapacidad()) {
            throw new RuntimeException("No hay asientos disponibles en este vuelo");
        }*/

        // Crear reserva
        Reserva reserva = new Reserva();
        reserva.setCodigoReserva(generarCodigoReserva());
        reserva.setVuelo(vuelo);
        reserva.setPasajero(pasajero);
        reserva.setAsientoPreferencia(request.getAsientoPreferencia());
        reserva.setPrecioTotal(tarifa.getPrecio());
        reserva.setEstadoReserva("PEN");
        reserva.setFechaReserva(LocalDateTime.now());

        Reserva savedReserva = reservaRepository.save(reserva);

        // Ocupar el asiento (RESERVADO, no OCUPADO hasta pagar)
        asientoService.ocuparAsiento(vuelo, request.getAsientoPreferencia(), savedReserva);

        // Convertir a DTO
        return convertToResponseDTO(savedReserva);
    }

    // Obtener reservas por ID de pasajero
    public List<ReservaResponseDTO> obtenerReservasPorPasajero(Integer idPasajero) {
        // Verificar que el pasajero existe
        Pasajero pasajero = pasajeroRepository.findById(idPasajero)
                .orElseThrow(() -> new RuntimeException("Pasajero no encontrado con ID: " + idPasajero));

        List<Reserva> reservas = reservaRepository.findByPasajeroIdPasajero(idPasajero);

        return reservas.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public ReservaResponseDTO obtenerReserva(String codigoReserva) {
        Reserva reserva = reservaRepository.findByCodigoReserva(codigoReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        return convertToResponseDTO(reserva);
    }

    @Transactional
    public void confirmarReserva(Reserva reserva){
        // Al confirmar el pago, cambiar asiento a OCUPADO
        asientoService.confirmarAsiento(reserva.getVuelo(), reserva.getAsientoPreferencia());
        reserva.setEstadoReserva("CONF");
        reservaRepository.save(reserva);
    }

    @Transactional
    public ReservaResponseDTO cancelarReserva(String codigoReserva) {
        Reserva reserva = obtenerReservaEntity(codigoReserva);
        if (reserva.getEstadoReserva().equals("CONF")) {
            throw new RuntimeException("No se puede cancelar una reserva confirmada");
        }

        // Liberar el asiento
        asientoService.liberarAsiento(reserva.getVuelo(), reserva.getAsientoPreferencia());

        reserva.setEstadoReserva("CANCELADA");
        Reserva savedReserva = reservaRepository.save(reserva);
        return convertToResponseDTO(savedReserva);
    }

    private Reserva obtenerReservaEntity(String codigoReserva) {
        return reservaRepository.findByCodigoReserva(codigoReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }

    private String generarCodigoReserva() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ReservaResponseDTO convertToResponseDTO(Reserva reserva) {
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setIdReserva(reserva.getIdReserva());
        dto.setCodigoReserva(reserva.getCodigoReserva());
        dto.setEstadoReserva(reserva.getEstadoReserva());
        dto.setFechaReserva(reserva.getFechaReserva());
        dto.setAsientoPreferencia(reserva.getAsientoPreferencia());
        dto.setPrecioTotal(reserva.getPrecioTotal());

        // Información del vuelo (solo campos necesarios)
        VueloInfoDTO vueloInfo = new VueloInfoDTO();
        vueloInfo.setIdVuelo(reserva.getVuelo().getIdVuelo());
        vueloInfo.setNumeroVuelo(reserva.getVuelo().getNumeroVuelo());
        vueloInfo.setOrigen(reserva.getVuelo().getOrigen());
        vueloInfo.setDestino(reserva.getVuelo().getDestino());
        vueloInfo.setFechaSalida(reserva.getVuelo().getFechaSalida());
        vueloInfo.setFechaLlegada(reserva.getVuelo().getFechaLlegada());
        vueloInfo.setAerolineaNombre(reserva.getVuelo().getAvion().getAerolinea().getNombreAerolinea());
        dto.setVuelo(vueloInfo);

        // Información del pasajero (solo campos necesarios)
        PasajeroInfoDTO pasajeroInfo = new PasajeroInfoDTO();
        pasajeroInfo.setIdPasajero(reserva.getPasajero().getIdPasajero());
        pasajeroInfo.setNombreCompleto(reserva.getPasajero().getNombreCompleto());
        pasajeroInfo.setNumPasaporte(reserva.getPasajero().getNumPasaporte());
        pasajeroInfo.setNacionalidad(reserva.getPasajero().getNacionalidad());
        dto.setPasajero(pasajeroInfo);

        return dto;
    }
}
