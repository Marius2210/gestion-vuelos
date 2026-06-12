package com.example.gestion_vuelos.service;

import com.example.gestion_vuelos.dto.AerolineaResponseDTO;
import com.example.gestion_vuelos.model.*;
import com.example.gestion_vuelos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminAerolineaService {

    @Autowired
    private AerolineaRepository aerolineaRepository;

    @Autowired
    private AvionRepository avionRepository;

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private TripulacionRepository tripulacionRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    // Listar todas las aerolíneas con DTO
    public List<AerolineaResponseDTO> listarTodasAerolineas() {
        List<Aerolinea> aerolineas = aerolineaRepository.findAll();
        return aerolineas.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Obtener aerolínea por ID
    public AerolineaResponseDTO obtenerAerolineaPorId(Integer id) {
        Aerolinea aerolinea = aerolineaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aerolínea no encontrada con ID: " + id));
        return convertToResponseDTO(aerolinea);
    }

    // Crear aerolínea
    public AerolineaResponseDTO crearAerolinea(Aerolinea aerolinea) {
        // Validar que el código IATA no exista
        if (aerolineaRepository.existsByCodigoIata(aerolinea.getCodigoIata())) {
            throw new RuntimeException("Ya existe una aerolínea con el código IATA: " + aerolinea.getCodigoIata());
        }
        Aerolinea savedAerolinea = aerolineaRepository.save(aerolinea);
        return convertToResponseDTO(savedAerolinea);
    }

    // Actualizar aerolínea
    public AerolineaResponseDTO actualizarAerolinea(Integer id, Aerolinea aerolineaActualizada) {
        Aerolinea aerolinea = aerolineaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aerolínea no encontrada con ID: " + id));

        aerolinea.setNombreAerolinea(aerolineaActualizada.getNombreAerolinea());
        aerolinea.setCodigoIata(aerolineaActualizada.getCodigoIata());

        Aerolinea updatedAerolinea = aerolineaRepository.save(aerolinea);
        return convertToResponseDTO(updatedAerolinea);
    }

    // Eliminar aerolinea
    @Transactional
    public void eliminarAerolinea(Integer id) {
        Aerolinea aerolinea = aerolineaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aerolínea no encontrada con ID: " + id));

        // Validación 1: Verificar si tiene vuelos activos
        List<Avion> aviones = avionRepository.findByAerolineaIdAerolinea(id);
        boolean tieneVuelosActivos = false;

        for (Avion avion : aviones) {
            List<Vuelo> vuelos = vueloRepository.findByAvionIdAvion(avion.getIdAvion());
            boolean vuelosActivos = vuelos.stream()
                    .anyMatch(v -> !"C".equals(v.getEstado())); // No cancelados
            if (vuelosActivos) {
                tieneVuelosActivos = true;
                break;
            }
        }

        if (tieneVuelosActivos) {
            throw new RuntimeException("No se puede eliminar la aerolínea porque tiene vuelos activos. " +
                    "Primero cancele o elimine los vuelos asociados.");
        }

        // Validación 2: Verificar si tiene tripulación asignada
        List<Tripulacion> tripulantes = tripulacionRepository.findByAerolineaIdAerolinea(id);
        if (!tripulantes.isEmpty()) {
            throw new RuntimeException("No se puede eliminar la aerolínea porque tiene " +
                    tripulantes.size() + " tripulantes asignados. Primero elimine la tripulación.");
        }

        // Validación 3: Verificar si tiene aviones con reservas
        for (Avion avion : aviones) {
            List<Vuelo> vuelos = vueloRepository.findByAvionIdAvion(avion.getIdAvion());
            for (Vuelo vuelo : vuelos) {
                List<Reserva> reservas = reservaRepository.findByVueloIdVuelo(vuelo.getIdVuelo());
                boolean reservasActivas = reservas.stream()
                        .anyMatch(r -> !"CANCELADA".equals(r.getEstadoReserva()));
                if (reservasActivas) {
                    throw new RuntimeException("No se puede eliminar la aerolínea porque hay reservas activas " +
                            "en vuelos asociados. Primero cancele las reservas.");
                }
            }
        }

        // Si pasa todas las validaciones, eliminar la aerolínea
        // (Los aviones se eliminarán en cascada por CascadeType.ALL)
        aerolineaRepository.delete(aerolinea);
    }

    // Verificar si una aerolínea tiene vuelos
    public boolean tieneVuelosActivos(Integer id) {
        List<Avion> aviones = avionRepository.findByAerolineaIdAerolinea(id);
        for (Avion avion : aviones) {
            List<Vuelo> vuelos = vueloRepository.findByAvionIdAvion(avion.getIdAvion());
            boolean tieneActivos = vuelos.stream()
                    .anyMatch(v -> !"C".equals(v.getEstado()));
            if (tieneActivos) {
                return true;
            }
        }
        return false;
    }

    // Eliminar aerolínea por código IATA
    @Transactional
    public void eliminarAerolineaPorCodigo(String codigoIata) {
        Aerolinea aerolinea = aerolineaRepository.findByCodigoIata(codigoIata)
                .orElseThrow(() -> new RuntimeException("Aerolínea no encontrada con código IATA: " + codigoIata));
        eliminarAerolinea(aerolinea.getIdAerolinea());
    }

    // Convertir a DTO
    private AerolineaResponseDTO convertToResponseDTO(Aerolinea aerolinea) {
        AerolineaResponseDTO dto = new AerolineaResponseDTO();
        dto.setIdAerolinea(aerolinea.getIdAerolinea());
        dto.setNombreAerolinea(aerolinea.getNombreAerolinea());
        dto.setCodigoIata(aerolinea.getCodigoIata());

        // Contar aviones (sin cargar toda la lista)
        if (aerolinea.getAviones() != null) {
            dto.setCantidadAviones(aerolinea.getAviones().size());
        }

        // Contar tripulantes
        if (aerolinea.getTripulantes() != null) {
            dto.setCantidadTripulantes(aerolinea.getTripulantes().size());
        }

        return dto;
    }
}
