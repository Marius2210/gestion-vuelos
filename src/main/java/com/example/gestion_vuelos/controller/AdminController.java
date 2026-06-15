package com.example.gestion_vuelos.controller;

import jakarta.validation.Valid;
import com.example.gestion_vuelos.dto.*;
import com.example.gestion_vuelos.service.*;
import com.example.gestion_vuelos.model.*;
import com.example.gestion_vuelos.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Administración", description = "Endpoints exclusivos para administradores")
public class AdminController {

    @Autowired
    private EstadisticaService estadisticaService;

    @Autowired
    private AdminUsuarioService adminUsuarioService;

    @Autowired
    private AerolineaRepository aerolineaRepository;

    @Autowired
    private AvionRepository avionRepository;

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private TarifaRepository tarifaRepository;

    @Autowired
    private AdminAerolineaService adminAerolineaService;

    @Autowired
    private AdminVueloService adminVueloService;

    @Autowired
    private AdminAvionService adminAvionService;

    @Autowired
    private AdminTripulacionService adminTripulacionService;

    @GetMapping("/estadisticas")
    @Operation(summary = "Estadísticas del sistema")
    public ResponseEntity<?> getEstadisticas() {
        return ResponseEntity.ok(estadisticaService.obtenerEstadisticas());
    }

    //USUARIOS
    @GetMapping("/usuarios")
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(adminUsuarioService.listarTodosLosUsuarios());
    }

    @GetMapping("/usuarios/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuario(@PathVariable Integer id) {
        return ResponseEntity.ok(adminUsuarioService.obtenerUsuarioPorId(id));
    }

    @PutMapping("/usuarios/{id}/estado")
    @Operation(summary = "Activar/Desactivar usuario")
    public ResponseEntity<UsuarioResponseDTO> toggleUsuarioEstado(@PathVariable Integer id) {
        return ResponseEntity.ok(adminUsuarioService.toggleUsuarioEstado(id));
    }

    @DeleteMapping("/usuarios/{id}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario y su pasajero asociado")
    public ResponseEntity<Map<String, String>> eliminarUsuario(@PathVariable Integer id) {
        adminUsuarioService.eliminarUsuario(id);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Usuario eliminado exitosamente");
        response.put("id", id.toString());
        return ResponseEntity.ok(response);
    }

    //AEROLINEAS
    @GetMapping("/aerolineas")
    @Operation(summary = "Listar todas las aerolíneas")
    public ResponseEntity<List<AerolineaResponseDTO>> listarAerolineas() {
        return ResponseEntity.ok(adminAerolineaService.listarTodasAerolineas());
    }

    @PostMapping("/aerolineas")
    @Operation(summary = "Crear aerolínea")
    public ResponseEntity<AerolineaResponseDTO> crearAerolinea(@RequestBody Aerolinea aerolinea) {
        return ResponseEntity.ok(adminAerolineaService.crearAerolinea(aerolinea));
    }

    @PutMapping("/aerolineas/{id}")
    @Operation(summary = "Actualizar aerolínea")
    public ResponseEntity<AerolineaResponseDTO> actualizarAerolinea(@PathVariable Integer id,
                                                                    @RequestBody Aerolinea aerolinea) {
        return ResponseEntity.ok(adminAerolineaService.actualizarAerolinea(id, aerolinea));
    }

    @DeleteMapping("/aerolineas/{id}")
    @Operation(summary = "Eliminar aerolínea")
    public ResponseEntity<Map<String, String>> eliminarAerolinea(@PathVariable Integer id) {
        adminAerolineaService.eliminarAerolinea(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Aerolínea eliminada exitosamente");
        response.put("id", id.toString());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/aerolineas/codigo/{codigoIata}")
    @Operation(summary = "Eliminar aerolínea por código IATA")
    public ResponseEntity<Map<String, String>> eliminarAerolineaPorCodigo(@PathVariable String codigoIata) {
        adminAerolineaService.eliminarAerolineaPorCodigo(codigoIata);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Aerolínea eliminada exitosamente");
        response.put("codigoIata", codigoIata);
        return ResponseEntity.ok(response);
    }

    //VUELOS
    @GetMapping("/vuelos")
    @Operation(summary = "Listar todos los vuelos")
    public ResponseEntity<List<VueloResponseDTO>> listarVuelos() {
        return ResponseEntity.ok(adminVueloService.listarTodosLosVuelos());
    }

    @GetMapping("/vuelos/{id}")
    @Operation(summary = "Obtener vuelo por ID")
    public ResponseEntity<VueloResponseDTO> obtenerVuelo(@PathVariable Integer id) {
        return ResponseEntity.ok(adminVueloService.obtenerVueloPorId(id));
    }

    @GetMapping("/vuelos/numero/{numeroVuelo}")
    @Operation(summary =  "Obtener vuelo por número")
    public ResponseEntity<VueloResponseDTO> obtenerVueloPorNumero(@PathVariable String numeroVuelo) {
        return ResponseEntity.ok(adminVueloService.obtenerVueloPorNumero(numeroVuelo));
    }

    @PostMapping("/vuelos")
    @Operation(summary = "Crear vuelo con tarifas")
    public ResponseEntity<VueloResponseDTO> crearVuelo(@RequestBody VueloCreationDTO vueloDTO) {
        return ResponseEntity.ok(adminVueloService.crearVuelo(vueloDTO));
    }

    @PutMapping("/vuelos/{id}")
    @Operation(summary = "Actualizar vuelo")
    public ResponseEntity<VueloResponseDTO> actualizarVuelo(@PathVariable Integer id,
                                                            @RequestBody VueloCreationDTO vueloDTO) {
        return ResponseEntity.ok(adminVueloService.actualizarVuelo(id, vueloDTO));
    }

    @DeleteMapping("/vuelos/{id}")
    @Operation(summary = "Eliminar vuelo")
    public ResponseEntity<Map<String, String>> eliminarVuelo(@PathVariable Integer id) {
        adminVueloService.eliminarVuelo(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Vuelo eliminado exitosamente");
        response.put("id", id.toString());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/vuelos/{id}/estado")
    @Operation(summary = "Cambiar estado del vuelo")
    public ResponseEntity<VueloResponseDTO> cambiarEstadoVuelo(@PathVariable Integer id,
                                                               @RequestParam String estado) {
        return ResponseEntity.ok(adminVueloService.cambiarEstadoVuelo(id, estado));
    }

    // AVION
    @GetMapping("/aviones")
    @Operation(summary = "Listar todos los aviones")
    public ResponseEntity<List<AvionResponseDTO>> listarAviones() {
        return ResponseEntity.ok(adminAvionService.listarTodosLosAviones());
    }

    @GetMapping("/aviones/aerolinea/{idAerolinea}")
    @Operation(summary = "Listar aviones por aerolínea")
    public ResponseEntity<List<AvionResponseDTO>> listarAvionesPorAerolinea(@PathVariable Integer idAerolinea) {
        return ResponseEntity.ok(adminAvionService.listarAvionesPorAerolinea(idAerolinea));
    }

    @GetMapping("/aviones/{id}")
    @Operation(summary = "Obtener avión por ID")
    public ResponseEntity<AvionResponseDTO> obtenerAvion(@PathVariable Integer id) {
        return ResponseEntity.ok(adminAvionService.obtenerAvionPorId(id));
    }

    @PostMapping("/aviones")
    @Operation(summary = "Crear avión")
    public ResponseEntity<AvionResponseDTO> crearAvion(@Valid @RequestBody AvionCreationDTO avionDTO) {
        return ResponseEntity.ok(adminAvionService.crearAvion(avionDTO));
    }

    @PutMapping("/aviones/{id}")
    @Operation(summary = "Actualizar avión")
    public ResponseEntity<AvionResponseDTO> actualizarAvion(@PathVariable Integer id,
                                                            @Valid @RequestBody AvionCreationDTO avionDTO) {
        return ResponseEntity.ok(adminAvionService.actualizarAvion(id, avionDTO));
    }

    @DeleteMapping("/aviones/{id}")
    @Operation(summary = "Eliminar avión")
    public ResponseEntity<Map<String, String>> eliminarAvion(@PathVariable Integer id) {
        adminAvionService.eliminarAvion(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Avión eliminado exitosamente");
        response.put("id", id.toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/aviones/{id}/estadisticas")
    @Operation(summary = "Obtener estadísticas del avión")
    public ResponseEntity<AvionResponseDTO> obtenerEstadisticasAvion(@PathVariable Integer id) {
        return ResponseEntity.ok(adminAvionService.obtenerEstadisticasAvion(id));
    }

    // TRIPULACION
    @GetMapping("/tripulacion")
    @Operation(summary = "Listar todos los tripulantes")
    public ResponseEntity<List<TripulacionResponseDTO>> listarTripulantes() {
        return ResponseEntity.ok(adminTripulacionService.listarTodosLosTripulantes());
    }

    @GetMapping("/tripulacion/aerolinea/{idAerolinea}")
    @Operation(summary = "Listar tripulantes por aerolínea")
    public ResponseEntity<List<TripulacionResponseDTO>> listarTripulantesPorAerolinea(@PathVariable Integer idAerolinea) {
        return ResponseEntity.ok(adminTripulacionService.listarTripulantesPorAerolinea(idAerolinea));
    }

    @GetMapping("/tripulacion/cargo/{cargo}")
    @Operation(summary = "Listar tripulantes por cargo")
    public ResponseEntity<List<TripulacionResponseDTO>> listarTripulantesPorCargo(@PathVariable String cargo) {
        return ResponseEntity.ok(adminTripulacionService.listarTripulantesPorCargo(cargo));
    }

    @GetMapping("/tripulacion/{id}")
    @Operation(summary = "Obtener tripulante por ID")
    public ResponseEntity<TripulacionResponseDTO> obtenerTripulante(@PathVariable Integer id) {
        return ResponseEntity.ok(adminTripulacionService.obtenerTripulantePorId(id));
    }

    @PostMapping("/tripulacion")
    @Operation(summary = "Crear tripulante")
    public ResponseEntity<TripulacionResponseDTO> crearTripulante(@Valid @RequestBody TripulacionCreationDTO tripulanteDTO) {
        return ResponseEntity.ok(adminTripulacionService.crearTripulante(tripulanteDTO));
    }

    @PutMapping("/tripulacion/{id}")
    @Operation(summary = "Actualizar tripulante")
    public ResponseEntity<TripulacionResponseDTO> actualizarTripulante(@PathVariable Integer id,
                                                                       @Valid @RequestBody TripulacionCreationDTO tripulanteDTO) {
        return ResponseEntity.ok(adminTripulacionService.actualizarTripulante(id, tripulanteDTO));
    }

    @DeleteMapping("/tripulacion/{id}")
    @Operation(summary = "Eliminar tripulante")
    public ResponseEntity<Map<String, String>> eliminarTripulante(@PathVariable Integer id) {
        adminTripulacionService.eliminarTripulante(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Tripulante eliminado exitosamente");
        response.put("id", id.toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tripulacion/{id}/estadisticas")
    @Operation(summary = "Obtener estadísticas del tripulante")
    public ResponseEntity<TripulacionResponseDTO> obtenerEstadisticasTripulante(@PathVariable Integer id) {
        return ResponseEntity.ok(adminTripulacionService.obtenerEstadisticasTripulante(id));
    }
}
