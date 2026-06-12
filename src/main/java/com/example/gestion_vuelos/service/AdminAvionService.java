package com.example.gestion_vuelos.service;

import com.example.gestion_vuelos.dto.AvionCreationDTO;
import com.example.gestion_vuelos.dto.AvionResponseDTO;
import com.example.gestion_vuelos.model.*;
import com.example.gestion_vuelos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminAvionService {

    @Autowired
    private AvionRepository avionRepository;

    @Autowired
    private AerolineaRepository aerolineaRepository;

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private TarifaRepository tarifaRepository;

    // Listar todos los aviones
    public List<AvionResponseDTO> listarTodosLosAviones() {
        List<Avion> aviones = avionRepository.findAll();
        return aviones.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Listar aviones por aerolínea
    public List<AvionResponseDTO> listarAvionesPorAerolinea(Integer idAerolinea) {
        // Verificar que la aerolínea existe
        aerolineaRepository.findById(idAerolinea)
                .orElseThrow(() -> new RuntimeException("Aerolínea no encontrada con ID: " + idAerolinea));

        List<Avion> aviones = avionRepository.findByAerolineaIdAerolinea(idAerolinea);
        return aviones.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Obtener avión por ID
    public AvionResponseDTO obtenerAvionPorId(Integer id) {
        Avion avion = avionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avión no encontrado con ID: " + id));
        return convertToResponseDTO(avion);
    }

    // Crear avión
    @Transactional
    public AvionResponseDTO crearAvion(AvionCreationDTO avionDTO) {
        // Validar que la aerolínea existe
        Aerolinea aerolinea = aerolineaRepository.findById(avionDTO.getIdAerolinea())
                .orElseThrow(() -> new RuntimeException("Aerolínea no encontrada con ID: " + avionDTO.getIdAerolinea()));

        // Validar capacidad
        if (avionDTO.getCapacidad() <= 0) {
            throw new RuntimeException("La capacidad debe ser mayor a 0");
        }

        // Crear el avión
        Avion avion = new Avion();
        avion.setModelo(avionDTO.getModelo());
        avion.setCapacidad(avionDTO.getCapacidad());
        avion.setAerolinea(aerolinea);

        Avion avionGuardado = avionRepository.save(avion);
        return convertToResponseDTO(avionGuardado);
    }

    // Actualizar avión
    @Transactional
    public AvionResponseDTO actualizarAvion(Integer id, AvionCreationDTO avionDTO) {
        Avion avion = avionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avión no encontrado con ID: " + id));

        // Validar que la aerolínea existe
        Aerolinea aerolinea = aerolineaRepository.findById(avionDTO.getIdAerolinea())
                .orElseThrow(() -> new RuntimeException("Aerolínea no encontrada con ID: " + avionDTO.getIdAerolinea()));

        // Validar capacidad
        if (avionDTO.getCapacidad() <= 0) {
            throw new RuntimeException("La capacidad debe ser mayor a 0");
        }

        // Si se está reduciendo la capacidad, verificar que no haya más reservas que la nueva capacidad
        if (avionDTO.getCapacidad() < avion.getCapacidad()) {
            List<Vuelo> vuelos = vueloRepository.findByAvionIdAvion(id);
            for (Vuelo vuelo : vuelos) {
                List<Reserva> reservasActivas = reservaRepository.findByVueloIdVuelo(vuelo.getIdVuelo()).stream()
                        .filter(r -> !"CANCELADA".equals(r.getEstadoReserva()))
                        .collect(Collectors.toList());

                if (reservasActivas.size() > avionDTO.getCapacidad()) {
                    throw new RuntimeException("No se puede reducir la capacidad a " + avionDTO.getCapacidad() +
                            " porque el avión tiene " + reservasActivas.size() + " reservas activas en sus vuelos.");
                }
            }
        }

        avion.setModelo(avionDTO.getModelo());
        avion.setCapacidad(avionDTO.getCapacidad());
        avion.setAerolinea(aerolinea);

        Avion avionActualizado = avionRepository.save(avion);
        return convertToResponseDTO(avionActualizado);
    }

    // Eliminar avión
    @Transactional
    public void eliminarAvion(Integer id) {
        Avion avion = avionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avión no encontrado con ID: " + id));

        // Validación 1: Verificar si tiene vuelos
        List<Vuelo> vuelos = vueloRepository.findByAvionIdAvion(id);

        if (!vuelos.isEmpty()) {
            // Verificar si hay vuelos con reservas activas
            boolean tieneVuelosConReservas = false;
            int totalReservasActivas = 0;

            for (Vuelo vuelo : vuelos) {
                List<Reserva> reservas = reservaRepository.findByVueloIdVuelo(vuelo.getIdVuelo());
                long reservasActivas = reservas.stream()
                        .filter(r -> !"CANCELADA".equals(r.getEstadoReserva()))
                        .count();

                if (reservasActivas > 0) {
                    tieneVuelosConReservas = true;
                    totalReservasActivas += reservasActivas;
                }
            }

            if (tieneVuelosConReservas) {
                throw new RuntimeException("No se puede eliminar el avión porque tiene " + vuelos.size() +
                        " vuelos asociados con " + totalReservasActivas + " reservas activas. " +
                        "Primero cancele o elimine los vuelos.");
            }

            // Si los vuelos no tienen reservas activas, se pueden eliminar primero
            for (Vuelo vuelo : vuelos) {
                // Eliminar tarifas del vuelo
                tarifaRepository.deleteByVueloIdVuelo(vuelo.getIdVuelo());
                // Eliminar relaciones de tripulación
                if (vuelo.getTripulantes() != null) {
                    vuelo.getTripulantes().clear();
                }
                // Eliminar el vuelo
                vueloRepository.delete(vuelo);
            }
        }

        // Finalmente eliminar el avión
        avionRepository.delete(avion);
    }

    // Obtener estadísticas del avión
    public AvionResponseDTO obtenerEstadisticasAvion(Integer id) {
        Avion avion = avionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avión no encontrado con ID: " + id));
        return convertToResponseDTO(avion);
    }

    // Convertir a DTO
    private AvionResponseDTO convertToResponseDTO(Avion avion) {
        AvionResponseDTO dto = new AvionResponseDTO();
        dto.setIdAvion(avion.getIdAvion());
        dto.setModelo(avion.getModelo());
        dto.setCapacidad(avion.getCapacidad());

        // Información de la aerolínea
        if (avion.getAerolinea() != null) {
            dto.setIdAerolinea(avion.getAerolinea().getIdAerolinea());
            dto.setAerolineaNombre(avion.getAerolinea().getNombreAerolinea());
            dto.setAerolineaCodigoIata(avion.getAerolinea().getCodigoIata());
        }

        // Contar vuelos asociados
        List<Vuelo> vuelos = vueloRepository.findByAvionIdAvion(avion.getIdAvion());
        dto.setCantidadVuelos(vuelos.size());

        // Contar vuelos activos (no cancelados)
        long vuelosActivos = vuelos.stream()
                .filter(v -> v.getEstado() != EstadoVuelo.C)
                .count();
        dto.setCantidadVuelosActivos((int) vuelosActivos);

        return dto;
    }
}
