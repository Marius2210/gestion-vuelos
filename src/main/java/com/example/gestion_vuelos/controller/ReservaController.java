package com.example.gestion_vuelos.controller;

import com.example.gestion_vuelos.dto.*;
import com.example.gestion_vuelos.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Gestión de reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping("/crear")
    @Operation(summary = "Crear reserva", description = "Genera una nueva reserva de vuelo")
    public ResponseEntity<?> crearReserva(@Valid @RequestBody ReservaRequest request) {
        return ResponseEntity.ok(reservaService.crearReserva(request));
    }

    // Obtener reservas por ID de pasajero
    @GetMapping("/pasajero/{idPasajero}")
    @Operation(summary = "Obtener reservas por pasajero", description = "Lista todas las reservas de un pasajero")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerReservasPorPasajero(@PathVariable Integer idPasajero) {
        return ResponseEntity.ok(reservaService.obtenerReservasPorPasajero(idPasajero));
    }

    @GetMapping("/{codigoReserva}")
    @Operation(summary = "Obtener reserva", description = "Consulta una reserva por su código")
    public ResponseEntity<?> obtenerReserva(@PathVariable String codigoReserva) {
        return ResponseEntity.ok(reservaService.obtenerReserva(codigoReserva));
    }

    @PutMapping("/cancelar/{codigoReserva}")
    @Operation(summary = "Cancelar reserva", description = "Cancela una reserva existente")
    public ResponseEntity<?> cancelarReserva(@PathVariable String codigoReserva) {
        return ResponseEntity.ok(reservaService.cancelarReserva(codigoReserva));
    }
}
