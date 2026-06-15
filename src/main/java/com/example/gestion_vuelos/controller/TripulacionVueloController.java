package com.example.gestion_vuelos.controller;

import com.example.gestion_vuelos.dto.AsignarTripulanteDTO;
import com.example.gestion_vuelos.dto.TripulanteAsignadoDTO;
import com.example.gestion_vuelos.model.Tripulacion;
import com.example.gestion_vuelos.model.Vuelo;
import com.example.gestion_vuelos.service.TripulacionVueloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administración - Tripulación en Vuelos", description = "Gestión de asignación de tripulación a vuelos")
public class TripulacionVueloController {

    @Autowired
    private TripulacionVueloService tripulacionVueloService;

    /**
     * Asignar tripulantes a un vuelo
     */
    @PostMapping("/vuelos/asignar-tripulantes")
    @Operation(summary = "Asignar tripulantes a un vuelo")
    public ResponseEntity<Map<String, String>> asignarTripulantes(@Valid @RequestBody AsignarTripulanteDTO request) {
        tripulacionVueloService.asignarTripulantesAVuelo(request.getIdVuelo(), request.getIdsTripulantes());

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Tripulantes asignados exitosamente al vuelo");
        response.put("idVuelo", request.getIdVuelo().toString());
        return ResponseEntity.ok(response);
    }

    /**
     * Remover un tripulante de un vuelo
     */
    @DeleteMapping("/vuelos/{idVuelo}/tripulantes/{idTripulante}")
    @Operation(summary = "Remover un tripulante de un vuelo")
    public ResponseEntity<Map<String, String>> removerTripulante(
            @PathVariable Integer idVuelo,
            @PathVariable Integer idTripulante) {

        tripulacionVueloService.removerTripulanteDeVuelo(idVuelo, idTripulante);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Tripulante removido exitosamente del vuelo");
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener tripulantes asignados a un vuelo
     */
    @GetMapping("/vuelos/{idVuelo}/tripulantes")
    @Operation(summary = "Obtener tripulantes asignados a un vuelo")
    public ResponseEntity<List<TripulanteAsignadoDTO>> getTripulantesByVuelo(@PathVariable Integer idVuelo) {
        return ResponseEntity.ok(tripulacionVueloService.getTripulantesByVuelo(idVuelo));
    }

    /**
     * Obtener vuelos asignados a un tripulante
     */
    @GetMapping("/tripulantes/{idTripulante}/vuelos")
    @Operation(summary = "Obtener vuelos asignados a un tripulante")
    public ResponseEntity<List<Vuelo>> getVuelosByTripulante(@PathVariable Integer idTripulante) {
        return ResponseEntity.ok(tripulacionVueloService.getVuelosByTripulante(idTripulante));
    }

    /**
     * Obtener tripulantes disponibles para un vuelo (no asignados)
     */
    @GetMapping("/vuelos/{idVuelo}/tripulantes-disponibles")
    @Operation(summary = "Obtener tripulantes disponibles para un vuelo")
    public ResponseEntity<List<Tripulacion>> getTripulantesDisponibles(@PathVariable Integer idVuelo) {
        return ResponseEntity.ok(tripulacionVueloService.getTripulantesDisponiblesParaVuelo(idVuelo));
    }
}
