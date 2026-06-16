package com.example.gestion_vuelos.config;

import com.example.gestion_vuelos.model.*;
import com.example.gestion_vuelos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.gestion_vuelos.service.AsientoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AerolineaRepository aerolineaRepository;

    @Autowired
    private AvionRepository avionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AsientoService asientoService;

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private AsientoVueloRepository asientoVueloRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear usuario ADMIN
        if (!usuarioRepository.existsByEmail("admin@aerolinea.com")) {
            Usuario admin = new Usuario();
            admin.setEmail("admin@aerolinea.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol("ADMIN");
            admin.setActivo(true);
            usuarioRepository.save(admin);
            System.out.println("Usuario ADMIN creado: admin@aerolinea.com / admin123");
        }

        // Crear aerolínea de prueba
        if (aerolineaRepository.count() == 0) {
            Aerolinea ava = new Aerolinea();
            ava.setNombreAerolinea("Avianca");
            ava.setCodigoIata("AVA");
            aerolineaRepository.save(ava);
        }

        // Inicializar asientos para vuelos existentes que no tengan asientos
        List<Vuelo> vuelos = vueloRepository.findAll();
        for (Vuelo vuelo : vuelos) {
            if (asientoVueloRepository.findByVuelo(vuelo).isEmpty()) {
                asientoService.inicializarAsientosParaVuelo(vuelo);
                System.out.println("Asientos inicializados para vuelo existente: " + vuelo.getNumeroVuelo());
            }
        }
    }
}
