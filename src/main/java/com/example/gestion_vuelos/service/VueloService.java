package com.example.gestion_vuelos.service;

import com.example.gestion_vuelos.dto.*;
import com.example.gestion_vuelos.model.*;
import com.example.gestion_vuelos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VueloService {

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private AerolineaRepository aerolineaRepository;

    public List<VueloDisponibleDTO> buscarVuelosDisponibles(BusquedaVueloRequest request) {
        List<Vuelo> vuelos = vueloRepository.findVuelosDisponibles(
                request.getOrigen(), request.getDestino(), request.getFechaSalida()
        );

        return vuelos.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    private VueloDisponibleDTO convertirADTO(Vuelo vuelo) {
        VueloDisponibleDTO dto = new VueloDisponibleDTO();
        dto.setIdVuelo(vuelo.getIdVuelo());
        dto.setNumeroVuelo(vuelo.getNumeroVuelo());
        dto.setAerolineaNombre(vuelo.getAvion().getAerolinea().getNombreAerolinea());
        dto.setOrigen(vuelo.getOrigen());
        dto.setDestino(vuelo.getDestino());
        dto.setFechaSalida(vuelo.getFechaSalida());
        dto.setFechaLlegada(vuelo.getFechaLlegada());
        dto.setAvionModelo(vuelo.getAvion().getModelo());
        dto.setCapacidad(vuelo.getAvion().getCapacidad());

        List<TarifaInfo> tarifas = vuelo.getTarifas().stream().map(t -> {
            TarifaInfo info = new TarifaInfo();
            info.setIdTarifa(t.getIdTarifa());
            info.setClase(t.getClase());
            info.setPrecio(t.getPrecio());
            return info;
        }).collect(Collectors.toList());
        dto.setTarifas(tarifas);

        return dto;
    }
}
