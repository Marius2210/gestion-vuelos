package com.example.gestion_vuelos.service;

import com.example.gestion_vuelos.dto.TripulacionCreationDTO;
import com.example.gestion_vuelos.dto.TripulacionResponseDTO;
import com.example.gestion_vuelos.model.*;
import com.example.gestion_vuelos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminTripulacionService {

    @Autowired
    private TripulacionRepository tripulacionRepository;

    @Autowired
    private AerolineaRepository aerolineaRepository;

    @Autowired
    private VueloRepository vueloRepository;

    // Listar todos los tripulantes
    public List<TripulacionResponseDTO> listarTodosLosTripulantes() {
        List<Tripulacion> tripulantes = tripulacionRepository.findAll();
        return tripulantes.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Listar tripulantes por aerolínea
    public List<TripulacionResponseDTO> listarTripulantesPorAerolinea(Integer idAerolinea) {
        // Verificar que la aerolínea existe
        aerolineaRepository.findById(idAerolinea)
                .orElseThrow(() -> new RuntimeException("Aerolínea no encontrada con ID: " + idAerolinea));

        List<Tripulacion> tripulantes = tripulacionRepository.findByAerolineaIdAerolinea(idAerolinea);
        return tripulantes.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Listar tripulantes por cargo
    public List<TripulacionResponseDTO> listarTripulantesPorCargo(String cargo) {
        List<Tripulacion> tripulantes = tripulacionRepository.findByCargoIgnoreCase(cargo);
        return tripulantes.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Obtener tripulante por ID
    public TripulacionResponseDTO obtenerTripulantePorId(Integer id) {
        Tripulacion tripulante = tripulacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tripulante no encontrado con ID: " + id));
        return convertToResponseDTO(tripulante);
    }

    // Crear tripulante
    @Transactional
    public TripulacionResponseDTO crearTripulante(TripulacionCreationDTO tripulanteDTO) {
        // Validar que la aerolínea existe
        Aerolinea aerolinea = aerolineaRepository.findById(tripulanteDTO.getIdAerolinea())
                .orElseThrow(() -> new RuntimeException("Aerolínea no encontrada con ID: " + tripulanteDTO.getIdAerolinea()));

        // Validar que el cargo sea válido
        String cargo = tripulanteDTO.getCargo().toLowerCase();
        if (!cargo.equals("piloto") && !cargo.equals("copiloto") &&
                !cargo.equals("sobrecargo") && !cargo.equals("tripulante de cabina")) {
            // Solo advertencia, no bloqueamos la creación
            System.out.println("Advertencia: Cargo no estándar: " + tripulanteDTO.getCargo());
        }

        // Crear el tripulante
        Tripulacion tripulante = new Tripulacion();
        tripulante.setNombre(tripulanteDTO.getNombre());
        tripulante.setCargo(tripulanteDTO.getCargo());
        tripulante.setAerolinea(aerolinea);

        Tripulacion tripulanteGuardado = tripulacionRepository.save(tripulante);
        return convertToResponseDTO(tripulanteGuardado);
    }

    // Actualizar tripulante
    @Transactional
    public TripulacionResponseDTO actualizarTripulante(Integer id, TripulacionCreationDTO tripulanteDTO) {
        Tripulacion tripulante = tripulacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tripulante no encontrado con ID: " + id));

        // Validar que la aerolínea existe si se está cambiando
        if (!tripulante.getAerolinea().getIdAerolinea().equals(tripulanteDTO.getIdAerolinea())) {
            Aerolinea aerolinea = aerolineaRepository.findById(tripulanteDTO.getIdAerolinea())
                    .orElseThrow(() -> new RuntimeException("Aerolínea no encontrada con ID: " + tripulanteDTO.getIdAerolinea()));
            tripulante.setAerolinea(aerolinea);
        }

        tripulante.setNombre(tripulanteDTO.getNombre());
        tripulante.setCargo(tripulanteDTO.getCargo());

        Tripulacion tripulanteActualizado = tripulacionRepository.save(tripulante);
        return convertToResponseDTO(tripulanteActualizado);
    }

    // Eliminar tripulante
    @Transactional
    public void eliminarTripulante(Integer id) {
        Tripulacion tripulante = tripulacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tripulante no encontrado con ID: " + id));

        // Validación: Verificar si el tripulante está asignado a algún vuelo
        List<Vuelo> vuelosAsignados = vueloRepository.findVuelosByTripulanteId(id);

        if (!vuelosAsignados.isEmpty()) {
            // Filtrar vuelos activos (no cancelados)
            List<Vuelo> vuelosActivos = vuelosAsignados.stream()
                    .filter(v -> v.getEstado() != EstadoVuelo.C)
                    .collect(Collectors.toList());

            if (!vuelosActivos.isEmpty()) {
                String vuelosInfo = vuelosActivos.stream()
                        .map(v -> v.getNumeroVuelo() + " (" + v.getOrigen() + " -> " + v.getDestino() + ")")
                        .collect(Collectors.joining(", "));

                throw new RuntimeException("No se puede eliminar el tripulante porque está asignado a " +
                        vuelosActivos.size() + " vuelo(s) activo(s): " + vuelosInfo + ". " +
                        "Primero remueva al tripulante de los vuelos.");
            }
        }

        // Si no tiene vuelos activos, eliminar el tripulante
        tripulacionRepository.delete(tripulante);
    }

    // Eliminar tripulante por nombre (útil para pruebas)
    @Transactional
    public void eliminarTripulantePorNombre(String nombre) {
        Tripulacion tripulante = tripulacionRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new RuntimeException("Tripulante no encontrado con nombre: " + nombre));
        eliminarTripulante(tripulante.getIdTripulante());
    }

    // Obtener estadísticas del tripulante
    public TripulacionResponseDTO obtenerEstadisticasTripulante(Integer id) {
        Tripulacion tripulante = tripulacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tripulante no encontrado con ID: " + id));
        return convertToResponseDTO(tripulante);
    }

    // Convertir a DTO
    private TripulacionResponseDTO convertToResponseDTO(Tripulacion tripulante) {
        TripulacionResponseDTO dto = new TripulacionResponseDTO();
        dto.setIdTripulante(tripulante.getIdTripulante());
        dto.setNombre(tripulante.getNombre());
        dto.setCargo(tripulante.getCargo());

        // Información de la aerolínea
        if (tripulante.getAerolinea() != null) {
            dto.setIdAerolinea(tripulante.getAerolinea().getIdAerolinea());
            dto.setAerolineaNombre(tripulante.getAerolinea().getNombreAerolinea());
            dto.setAerolineaCodigoIata(tripulante.getAerolinea().getCodigoIata());
        }

        // Contar vuelos asignados
        List<Vuelo> vuelosAsignados = vueloRepository.findVuelosByTripulanteId(tripulante.getIdTripulante());
        dto.setCantidadVuelosAsignados(vuelosAsignados.size());

        return dto;
    }
}
