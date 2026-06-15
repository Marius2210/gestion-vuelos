package com.example.gestion_vuelos.controller;

import com.example.gestion_vuelos.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.gestion_vuelos.service.ReclamoService;
import java.util.List;

@RestController
@RequestMapping("/api/reclamos")
@Tag(name = "Reclamos", description = "Gestión de reclamos")
public class ReclamoController {

    @Autowired
    private ReclamoService reclamoService;

    @PostMapping("/enviar")
    @Operation(summary = "Enviar reclamo", description = "Crea un nuevo reclamo asociado a una reserva")
    public ResponseEntity<ReclamoResponseDTO> enviarReclamo(@Valid @RequestBody ReclamoRequest request) {
        return ResponseEntity.ok(reclamoService.crearReclamo(request));
    }

    @GetMapping("/{idReclamo}")
    @Operation(summary = "Obtener reclamo por ID", description = "Consulta un reclamo específico")
    public ResponseEntity<ReclamoResponseDTO> obtenerReclamo(@PathVariable Integer idReclamo) {
        return ResponseEntity.ok(reclamoService.obtenerReclamo(idReclamo));
    }

    @GetMapping("/reserva/{idReserva}")
    @Operation(summary = "Obtener reclamos por reserva", description = "Lista todos los reclamos de una reserva")
    public ResponseEntity<List<ReclamoResponseDTO>> obtenerReclamosPorReserva(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(reclamoService.obtenerReclamosPorReserva(idReserva));
    }
}
