package com.example.gestion_vuelos.controller;

import com.example.gestion_vuelos.dto.*;
import com.example.gestion_vuelos.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagos")
@Tag(name = "Pagos", description = "Gestión de pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @PostMapping("/confirmar")
    @Operation(summary = "Confirmar pago", description = "Registra el pago de una reserva")
    public ResponseEntity<PagoResponseDTO> confirmarPago(@Valid @RequestBody PagoRequest request) {
        return ResponseEntity.ok(pagoService.confirmarPago(request));
    }

    @GetMapping("/{idPago}")
    @Operation(summary = "Obtener pago por ID", description = "Consulta un pago específico")
    public ResponseEntity<PagoResponseDTO> obtenerPago(@PathVariable Integer idPago) {
        return ResponseEntity.ok(pagoService.obtenerPago(idPago));
    }

    @GetMapping("/reserva/{idReserva}")
    @Operation(summary = "Obtener pago por reserva", description = "Consulta el pago asociado a una reserva")
    public ResponseEntity<PagoResponseDTO> obtenerPagoPorReserva(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(pagoService.obtenerPagoPorReserva(idReserva));
    }
}
