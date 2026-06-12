package com.example.gestion_vuelos.service;

import com.example.gestion_vuelos.dto.TripulanteAsignadoDTO;
import com.example.gestion_vuelos.model.*;
import com.example.gestion_vuelos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripulacionVueloService {

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private TripulacionRepository tripulacionRepository;

    /**
     * Asignar tripulantes a un vuelo
     */
    @Transactional
    public void asignarTripulantesAVuelo(Integer idVuelo, List<Integer> idsTripulantes) {
        Vuelo vuelo = vueloRepository.findById(idVuelo)
                .orElseThrow(() -> new RuntimeException("Vuelo no encontrado con ID: " + idVuelo));

        // Validar que el vuelo no esté cancelado
        if (vuelo.getEstado() == EstadoVuelo.C) {
            throw new RuntimeException("No se puede asignar tripulación a un vuelo cancelado");
        }

        // Validar que el vuelo no haya salido ya
        if (vuelo.getFechaSalida().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("No se puede asignar tripulación a un vuelo que ya salió");
        }

        List<Tripulacion> tripulantes = tripulacionRepository.findAllById(idsTripulantes);

        if (tripulantes.size() != idsTripulantes.size()) {
            throw new RuntimeException("Algunos tripulantes no existen");
        }

        // Validar que todos los tripulantes pertenezcan a la misma aerolínea del vuelo
        String aerolineaEsperada = vuelo.getAvion().getAerolinea().getNombreAerolinea();
        for (Tripulacion tripulante : tripulantes) {
            if (!tripulante.getAerolinea().getNombreAerolinea().equals(aerolineaEsperada)) {
                throw new RuntimeException("El tripulante " + tripulante.getNombre() +
                        " no pertenece a la aerolínea " + aerolineaEsperada);
            }
        }

        // Agregar tripulantes al vuelo (evitando duplicados)
        for (Tripulacion tripulante : tripulantes) {
            if (!vuelo.getTripulantes().contains(tripulante)) {
                vuelo.getTripulantes().add(tripulante);
            }
        }

        vueloRepository.save(vuelo);
    }

    /**
     * Remover un tripulante de un vuelo
     */
    @Transactional
    public void removerTripulanteDeVuelo(Integer idVuelo, Integer idTripulante) {
        Vuelo vuelo = vueloRepository.findById(idVuelo)
                .orElseThrow(() -> new RuntimeException("Vuelo no encontrado con ID: " + idVuelo));

        Tripulacion tripulante = tripulacionRepository.findById(idTripulante)
                .orElseThrow(() -> new RuntimeException("Tripulante no encontrado con ID: " + idTripulante));

        if (!vuelo.getTripulantes().contains(tripulante)) {
            throw new RuntimeException("El tripulante no está asignado a este vuelo");
        }

        vuelo.getTripulantes().remove(tripulante);
        vueloRepository.save(vuelo);
    }

    /**
     * Obtener tripulantes asignados a un vuelo
     */
    public List<TripulanteAsignadoDTO> getTripulantesByVuelo(Integer idVuelo) {
        Vuelo vuelo = vueloRepository.findById(idVuelo)
                .orElseThrow(() -> new RuntimeException("Vuelo no encontrado con ID: " + idVuelo));

        return vuelo.getTripulantes().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener vuelos asignados a un tripulante
     */
    public List<Vuelo> getVuelosByTripulante(Integer idTripulante) {
        tripulacionRepository.findById(idTripulante)
                .orElseThrow(() -> new RuntimeException("Tripulante no encontrado con ID: " + idTripulante));

        return vueloRepository.findVuelosByTripulanteId(idTripulante);
    }

    /**
     * Obtener tripulantes disponibles para un vuelo (que no estén ya asignados)
     */
    public List<Tripulacion> getTripulantesDisponiblesParaVuelo(Integer idVuelo) {
        Vuelo vuelo = vueloRepository.findById(idVuelo)
                .orElseThrow(() -> new RuntimeException("Vuelo no encontrado con ID: " + idVuelo));

        String aerolineaNombre = vuelo.getAvion().getAerolinea().getNombreAerolinea();

        // Obtener todos los tripulantes de la aerolínea que NO están en este vuelo
        List<Tripulacion> todosTripulantes = tripulacionRepository.findByAerolineaIdAerolinea(
                vuelo.getAvion().getAerolinea().getIdAerolinea());

        return todosTripulantes.stream()
                .filter(t -> !vuelo.getTripulantes().contains(t))
                .collect(Collectors.toList());
    }

    private TripulanteAsignadoDTO convertToDTO(Tripulacion tripulante) {
        TripulanteAsignadoDTO dto = new TripulanteAsignadoDTO();
        dto.setIdTripulante(tripulante.getIdTripulante());
        dto.setNombre(tripulante.getNombre());
        dto.setCargo(tripulante.getCargo());
        dto.setAerolineaNombre(tripulante.getAerolinea().getNombreAerolinea());
        return dto;
    }
}
