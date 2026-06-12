package com.example.gestion_vuelos.service;

import com.example.gestion_vuelos.dto.*;
import com.example.gestion_vuelos.model.*;
import com.example.gestion_vuelos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminVueloService {

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private AvionRepository avionRepository;

    @Autowired
    private TarifaRepository tarifaRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private TripulacionRepository tripulacionRepository;

    @Autowired
    private AsientoService asientoService;

    @Autowired
    private AsientoVueloRepository asientoVueloRepository;

    // Listar todos los vuelos
    public List<VueloResponseDTO> listarTodosLosVuelos() {
        List<Vuelo> vuelos = vueloRepository.findAll();
        return vuelos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Obtener vuelo por ID
    public VueloResponseDTO obtenerVueloPorId(Integer id) {
        Vuelo vuelo = vueloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vuelo no encontrado con ID: " + id));
        return convertToResponseDTO(vuelo);
    }

    // Obtener vuelo por número de vuelo
    public VueloResponseDTO obtenerVueloPorNumero(String numeroVuelo) {
        Vuelo vuelo = vueloRepository.findByNumeroVuelo(numeroVuelo)
                .orElseThrow(() -> new RuntimeException("Vuelo no encontrado con número: " + numeroVuelo));
        return convertToResponseDTO(vuelo);
    }

    // Crear vuelo con tarifas
    @Transactional
    public VueloResponseDTO crearVuelo(VueloCreationDTO vueloDTO) {
        // Validar que el avión existe
        Avion avion = avionRepository.findById(vueloDTO.getIdAvion())
                .orElseThrow(() -> new RuntimeException("Avión no encontrado con ID: " + vueloDTO.getIdAvion()));

        // Validar que el número de vuelo no exista
        if (vueloRepository.existsByNumeroVuelo(vueloDTO.getNumeroVuelo())) {
            throw new RuntimeException("Ya existe un vuelo con el número: " + vueloDTO.getNumeroVuelo());
        }

        // Validar fechas
        if (vueloDTO.getFechaLlegada().isBefore(vueloDTO.getFechaSalida())) {
            throw new RuntimeException("La fecha de llegada no puede ser anterior a la fecha de salida");
        }

        // Crear el vuelo
        Vuelo vuelo = new Vuelo();
        vuelo.setNumeroVuelo(vueloDTO.getNumeroVuelo());
        vuelo.setOrigen(vueloDTO.getOrigen());
        vuelo.setDestino(vueloDTO.getDestino());
        vuelo.setFechaSalida(vueloDTO.getFechaSalida());
        vuelo.setFechaLlegada(vueloDTO.getFechaLlegada());
        vuelo.setAvion(avion);

        // Convertir estado
        if (vueloDTO.getEstado() != null) {
            try {
                vuelo.setEstado(EstadoVuelo.valueOf(vueloDTO.getEstado()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Estado inválido. Use: P, V, A, C");
            }
        }

        Vuelo vueloGuardado = vueloRepository.save(vuelo);

        // INICIALIZAR ASIENTOS PARA EL VUELO NUEVO
        asientoService.inicializarAsientosParaVuelo(vueloGuardado);

        // Crear tarifas
        if (vueloDTO.getTarifas() != null && !vueloDTO.getTarifas().isEmpty()) {
            for (VueloCreationDTO.TarifaCreationDTO tarifaDTO : vueloDTO.getTarifas()) {
                Tarifa tarifa = new Tarifa();
                tarifa.setClase(tarifaDTO.getClase());
                tarifa.setPrecio(tarifaDTO.getPrecio());
                tarifa.setVuelo(vueloGuardado);
                tarifaRepository.save(tarifa);
            }
        }

        return convertToResponseDTO(vueloGuardado);
    }

    // Actualizar vuelo
    @Transactional
    public VueloResponseDTO actualizarVuelo(Integer id, VueloCreationDTO vueloDTO) {
        Vuelo vuelo = vueloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vuelo no encontrado con ID: " + id));

        // Validar que el avión existe
        Avion avion = avionRepository.findById(vueloDTO.getIdAvion())
                .orElseThrow(() -> new RuntimeException("Avión no encontrado con ID: " + vueloDTO.getIdAvion()));

        // Validar que el número de vuelo no esté en uso por otro vuelo
        if (!vuelo.getNumeroVuelo().equals(vueloDTO.getNumeroVuelo()) &&
                vueloRepository.existsByNumeroVuelo(vueloDTO.getNumeroVuelo())) {
            throw new RuntimeException("Ya existe un vuelo con el número: " + vueloDTO.getNumeroVuelo());
        }

        // Validar fechas
        if (vueloDTO.getFechaLlegada().isBefore(vueloDTO.getFechaSalida())) {
            throw new RuntimeException("La fecha de llegada no puede ser anterior a la fecha de salida");
        }

        // Actualizar datos
        vuelo.setNumeroVuelo(vueloDTO.getNumeroVuelo());
        vuelo.setOrigen(vueloDTO.getOrigen());
        vuelo.setDestino(vueloDTO.getDestino());
        vuelo.setFechaSalida(vueloDTO.getFechaSalida());
        vuelo.setFechaLlegada(vueloDTO.getFechaLlegada());
        vuelo.setAvion(avion);

        if (vueloDTO.getEstado() != null) {
            try {
                vuelo.setEstado(EstadoVuelo.valueOf(vueloDTO.getEstado()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Estado inválido. Use: P, V, A, C");
            }
        }

        Vuelo vueloActualizado = vueloRepository.save(vuelo);
        return convertToResponseDTO(vueloActualizado);
    }

    // Eliminar vuelo
    @Transactional
    public void eliminarVuelo(Integer id) {
        Vuelo vuelo = vueloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vuelo no encontrado con ID: " + id));

        // Validación 1: Verificar si tiene reservas activas
        List<Reserva> reservas = reservaRepository.findByVueloIdVuelo(id);
        boolean tieneReservasActivas = reservas.stream()
                .anyMatch(r -> !"CANCELADA".equals(r.getEstadoReserva()));

        if (tieneReservasActivas) {
            long reservasActivasCount = reservas.stream()
                    .filter(r -> !"CANCELADA".equals(r.getEstadoReserva()))
                    .count();
            throw new RuntimeException("No se puede eliminar el vuelo porque tiene " +
                    reservasActivasCount + " reservas activas. Primero cancele las reservas.");
        }

        // Eliminar asientos del vuelo (FOREIGN KEY constraint)
        asientoVueloRepository.deleteByVueloId(id);

        // Validación 2: Si hay reservas canceladas, desasociarlas del vuelo
        if (!reservas.isEmpty()) {
            for (Reserva reserva : reservas) {
                reserva.setVuelo(null);
                reservaRepository.save(reserva);
            }
        }

        // Validación 3: Eliminar las tarifas directamente por ID del vuelo
        // Esta es la forma más segura sin tocar la lista del vuelo
        tarifaRepository.deleteByVueloIdVuelo(id);

        // Validación 4: Limpiar relación con tripulación (ManyToMany)
        if (vuelo.getTripulantes() != null) {
            vuelo.getTripulantes().clear();
            vueloRepository.save(vuelo);
        }

        // Finalmente eliminar el vuelo
        vueloRepository.deleteById(id);
    }

    // Cambiar estado del vuelo
    @Transactional
    public VueloResponseDTO cambiarEstadoVuelo(Integer id, String nuevoEstado) {
        Vuelo vuelo = vueloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vuelo no encontrado con ID: " + id));

        try {
            EstadoVuelo estado = EstadoVuelo.valueOf(nuevoEstado);
            vuelo.setEstado(estado);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido. Use: P (Programado), V (En Vuelo), A (Aterrizado), C (Cancelado)");
        }

        Vuelo vueloActualizado = vueloRepository.save(vuelo);
        return convertToResponseDTO(vueloActualizado);
    }

    // Convertir a DTO
    private VueloResponseDTO convertToResponseDTO(Vuelo vuelo) {
        VueloResponseDTO dto = new VueloResponseDTO();
        dto.setIdVuelo(vuelo.getIdVuelo());
        dto.setNumeroVuelo(vuelo.getNumeroVuelo());
        dto.setOrigen(vuelo.getOrigen());
        dto.setDestino(vuelo.getDestino());
        dto.setFechaSalida(vuelo.getFechaSalida());
        dto.setFechaLlegada(vuelo.getFechaLlegada());
        dto.setEstado(vuelo.getEstado().name());

        // Descripción del estado
        switch (vuelo.getEstado()) {
            case P: dto.setEstadoDescripcion("Programado"); break;
            case V: dto.setEstadoDescripcion("En Vuelo"); break;
            case A: dto.setEstadoDescripcion("Aterrizado"); break;
            case C: dto.setEstadoDescripcion("Cancelado"); break;
        }

        // Información del avión
        if (vuelo.getAvion() != null) {
            dto.setIdAvion(vuelo.getAvion().getIdAvion());
            dto.setAvionModelo(vuelo.getAvion().getModelo());
            dto.setCapacidad(vuelo.getAvion().getCapacidad());

            if (vuelo.getAvion().getAerolinea() != null) {
                dto.setAerolineaNombre(vuelo.getAvion().getAerolinea().getNombreAerolinea());
            }
        }

        // Tarifas
        if (vuelo.getTarifas() != null && !vuelo.getTarifas().isEmpty()) {
            List<TarifaInfo> tarifasDTO = vuelo.getTarifas().stream().map(tarifa -> {
                TarifaInfo tarifaDTO = new TarifaInfo();
                tarifaDTO.setIdTarifa(tarifa.getIdTarifa());
                tarifaDTO.setClase(tarifa.getClase());
                tarifaDTO.setPrecio(tarifa.getPrecio());
                return tarifaDTO;
            }).collect(Collectors.toList());
            dto.setTarifas(tarifasDTO);
        }

        // Cantidad de reservas
        if (vuelo.getReservas() != null) {
            dto.setCantidadReservas(vuelo.getReservas().size());
        } else {
            dto.setCantidadReservas(0);
        }

        return dto;
    }
}
