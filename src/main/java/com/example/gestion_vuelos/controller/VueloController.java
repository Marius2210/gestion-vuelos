package com.example.gestion_vuelos.controller;

import com.example.gestion_vuelos.dto.*;
import com.example.gestion_vuelos.service.AsientoService;
import com.example.gestion_vuelos.service.VueloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vuelos")
@Tag(name = "Vuelos", description = "Gestión de vuelos y búsqueda")
public class VueloController {

    @Autowired
    private VueloService vueloService;

    @Autowired
    private AsientoService asientoService;

    @PostMapping("/disponibles")
    @Operation(summary = "Buscar vuelos disponibles", description = "Lista vuelos según origen, destino y fecha")
    public ResponseEntity<?> buscarVuelos(@Valid @RequestBody BusquedaVueloRequest request) {
        return ResponseEntity.ok(vueloService.buscarVuelosDisponibles(request));
    }

    @GetMapping("/{idVuelo}/asientos/mapa")
    @Operation(summary = "Obtener mapa de asientos del vuelo")
    public ResponseEntity<MapaAsientosDTO> obtenerMapaAsientos(@PathVariable Integer idVuelo) {
        return ResponseEntity.ok(asientoService.obtenerMapaAsientos(idVuelo));
    }

    @GetMapping("/{idVuelo}/asientos/disponibles")
    @Operation(summary = "Obtener asientos disponibles")
    public ResponseEntity<List<AsientoDTO>> obtenerAsientosDisponibles(
            @PathVariable Integer idVuelo,
            @RequestParam(required = false) String tipo) {
        return ResponseEntity.ok(asientoService.obtenerAsientosDisponiblesPorTipo(idVuelo, tipo));
    }
}
