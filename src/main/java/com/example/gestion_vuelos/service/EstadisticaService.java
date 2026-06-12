package com.example.gestion_vuelos.service;

import com.example.gestion_vuelos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class EstadisticaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ReclamoRepository reclamoRepository;

    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();

        long totalReservas = reservaRepository.count();
        long reservasConfirmadas = reservaRepository.findAll().stream()
                .filter(r -> r.getEstadoReserva().equals("CONF")).count();
        long reservasCanceladas = reservaRepository.findAll().stream()
                .filter(r -> r.getEstadoReserva().equals("CANCELADA")).count();

        stats.put("totalReservas", totalReservas);
        stats.put("reservasConfirmadas", reservasConfirmadas);
        stats.put("reservasCanceladas", reservasCanceladas);
        stats.put("vuelosActivos", vueloRepository.findAll().size());
        stats.put("totalPagos", pagoRepository.count());
        stats.put("totalReclamos", reclamoRepository.count());

        return stats;
    }
}
