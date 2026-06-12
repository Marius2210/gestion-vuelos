package com.example.gestion_vuelos.service;

import com.example.gestion_vuelos.dto.AsientoDTO;
import com.example.gestion_vuelos.dto.MapaAsientosDTO;
import com.example.gestion_vuelos.model.*;
import com.example.gestion_vuelos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AsientoService {

    @Autowired
    private AsientoVueloRepository asientoVueloRepository;

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    // Inicializar asientos para un vuelo nuevo
    @Transactional
    public void inicializarAsientosParaVuelo(Vuelo vuelo) {
        int capacidad = vuelo.getAvion().getCapacidad();
        int asientosPorFila = 6; // A, B, C, D, E, F
        int numeroFilas = (int) Math.ceil((double) capacidad / asientosPorFila);

        List<AsientoVuelo> asientos = new ArrayList<>();

        for (int fila = 1; fila <= numeroFilas; fila++) {
            // Letras A, B, C, D, E, F
            for (char letra : new char[]{'A', 'B', 'C', 'D', 'E', 'F'}) {
                // Si ya superamos la capacidad, no crear más asientos
                if (asientos.size() >= capacidad) break;

                AsientoVuelo asiento = new AsientoVuelo();
                asiento.setVuelo(vuelo);
                asiento.setNumeroAsiento(fila + String.valueOf(letra));
                asiento.setFila(fila);
                asiento.setLetra(String.valueOf(letra));
                asiento.setTipo(determinarTipoAsiento(fila, letra, asientosPorFila));
                asiento.setEstado(AsientoEstado.DISPONIBLE);
                asientos.add(asiento);
            }
        }

        asientoVueloRepository.saveAll(asientos);
        System.out.println("Inicializados " + asientos.size() + " asientos para el vuelo " + vuelo.getNumeroVuelo());
    }

    // Determinar tipo de asiento (VENTANA, PASILLO, CENTRO)
    private String determinarTipoAsiento(int fila, char letra, int asientosPorFila) {
        if (letra == 'A' || letra == 'F') {
            return "VENTANA";
        } else if (letra == 'C' || letra == 'D') {
            return "PASILLO";
        } else {
            return "CENTRO";
        }
    }

    // Validar si un asiento está disponible
    public boolean isAsientoDisponible(Vuelo vuelo, String numeroAsiento) {
        Optional<AsientoVuelo> asientoOpt = asientoVueloRepository.findByVueloAndNumeroAsiento(vuelo, numeroAsiento);

        if (asientoOpt.isEmpty()) {
            return false; // El asiento no existe en este vuelo
        }

        return asientoOpt.get().getEstado() == AsientoEstado.DISPONIBLE;
    }

    // Validar formato de asiento
    public boolean validarFormatoAsiento(String numeroAsiento, int capacidad) {
        if (numeroAsiento == null || !numeroAsiento.matches("^[0-9]{1,2}[A-F]$")) {
            return false;
        }

        // Extraer número de fila
        int fila = Integer.parseInt(numeroAsiento.substring(0, numeroAsiento.length() - 1));
        int asientosPorFila = 6;
        int maxFilas = (int) Math.ceil((double) capacidad / asientosPorFila);

        return fila >= 1 && fila <= maxFilas;
    }

    // Obtener asiento por vuelo y número
    public AsientoVuelo obtenerAsiento(Vuelo vuelo, String numeroAsiento) {
        return asientoVueloRepository.findByVueloAndNumeroAsiento(vuelo, numeroAsiento)
                .orElseThrow(() -> new RuntimeException("El asiento " + numeroAsiento + " no existe para este vuelo"));
    }

    // Ocupar asiento (al crear reserva)
    @Transactional
    public void ocuparAsiento(Vuelo vuelo, String numeroAsiento, Reserva reserva) {
        AsientoVuelo asiento = obtenerAsiento(vuelo, numeroAsiento);

        if (asiento.getEstado() != AsientoEstado.DISPONIBLE) {
            throw new RuntimeException("El asiento " + numeroAsiento + " no está disponible");
        }

        asiento.setEstado(AsientoEstado.RESERVADO);
        asiento.setReserva(reserva);
        asientoVueloRepository.save(asiento);
    }

    // Liberar asiento (al cancelar reserva)
    @Transactional
    public void liberarAsiento(Vuelo vuelo, String numeroAsiento) {
        AsientoVuelo asiento = obtenerAsiento(vuelo, numeroAsiento);
        asiento.setEstado(AsientoEstado.DISPONIBLE);
        asiento.setReserva(null);
        asientoVueloRepository.save(asiento);
    }

    // Confirmar asiento (al pagar)
    @Transactional
    public void confirmarAsiento(Vuelo vuelo, String numeroAsiento) {
        AsientoVuelo asiento = obtenerAsiento(vuelo, numeroAsiento);
        if (asiento.getEstado() == AsientoEstado.RESERVADO) {
            asiento.setEstado(AsientoEstado.OCUPADO);
            asientoVueloRepository.save(asiento);
        }
    }

    // Obtener mapa de asientos del vuelo
    public MapaAsientosDTO obtenerMapaAsientos(Integer idVuelo) {
        Vuelo vuelo = vueloRepository.findById(idVuelo)
                .orElseThrow(() -> new RuntimeException("Vuelo no encontrado"));

        List<AsientoVuelo> asientos = asientoVueloRepository.findByVuelo(vuelo);

        MapaAsientosDTO mapa = new MapaAsientosDTO();
        mapa.setIdVuelo(vuelo.getIdVuelo());
        mapa.setNumeroVuelo(vuelo.getNumeroVuelo());
        mapa.setOrigen(vuelo.getOrigen());
        mapa.setDestino(vuelo.getDestino());
        mapa.setCapacidad(vuelo.getAvion().getCapacidad());
        mapa.setAsientosDisponibles((int) asientos.stream()
                .filter(a -> a.getEstado() == AsientoEstado.DISPONIBLE).count());
        mapa.setAsientosOcupados((int) asientos.stream()
                .filter(a -> a.getEstado() != AsientoEstado.DISPONIBLE).count());

        // Organizar asientos por fila
        Map<Integer, List<AsientoDTO>> asientosPorFila = new TreeMap<>();

        for (AsientoVuelo asiento : asientos) {
            AsientoDTO dto = new AsientoDTO();
            dto.setIdAsientoVuelo(asiento.getIdAsientoVuelo());
            dto.setNumeroAsiento(asiento.getNumeroAsiento());
            dto.setFila(asiento.getFila());
            dto.setLetra(asiento.getLetra());
            dto.setTipo(asiento.getTipo());
            dto.setEstado(asiento.getEstado());

            if (asiento.getReserva() != null) {
                dto.setReservaCodigo(asiento.getReserva().getCodigoReserva());
            }

            asientosPorFila.computeIfAbsent(asiento.getFila(), k -> new ArrayList<>()).add(dto);
        }

        mapa.setAsientosPorFila(asientosPorFila);
        return mapa;
    }

    // Obtener asientos disponibles de un tipo específico
    public List<AsientoDTO> obtenerAsientosDisponiblesPorTipo(Integer idVuelo, String tipo) {
        Vuelo vuelo = vueloRepository.findById(idVuelo)
                .orElseThrow(() -> new RuntimeException("Vuelo no encontrado"));

        return asientoVueloRepository.findByVueloAndEstado(vuelo, AsientoEstado.DISPONIBLE)
                .stream()
                .filter(a -> tipo == null || a.getTipo().equalsIgnoreCase(tipo))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AsientoDTO convertToDTO(AsientoVuelo asiento) {
        AsientoDTO dto = new AsientoDTO();
        dto.setIdAsientoVuelo(asiento.getIdAsientoVuelo());
        dto.setNumeroAsiento(asiento.getNumeroAsiento());
        dto.setFila(asiento.getFila());
        dto.setLetra(asiento.getLetra());
        dto.setTipo(asiento.getTipo());
        dto.setEstado(asiento.getEstado());
        return dto;
    }
}
