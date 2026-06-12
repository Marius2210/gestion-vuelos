package com.example.gestion_vuelos.service;

import com.example.gestion_vuelos.dto.*;
import com.example.gestion_vuelos.model.*;
import com.example.gestion_vuelos.repository.*;
import com.example.gestion_vuelos.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasajeroRepository pasajeroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol());

        // Buscar el pasajero asociado al usuario
        Integer idPasajero = null;
        String nombrePasajero = null;

        // Buscar si el usuario tiene un pasajero asociado
        if (usuario.getPasajero() != null) {
            idPasajero = usuario.getPasajero().getIdPasajero();
            nombrePasajero = usuario.getPasajero().getNombreCompleto();
        } else {
            // Buscar por email en la tabla pasajero
            Optional<Pasajero> pasajeroOpt = pasajeroRepository.findByEmail(usuario.getEmail());
            if (pasajeroOpt.isPresent()) {
                Pasajero pasajero = pasajeroOpt.get();
                idPasajero = pasajero.getIdPasajero();
                nombrePasajero = pasajero.getNombreCompleto();
            }
        }

        return new AuthResponse(token, usuario.getEmail(), usuario.getRol(), idPasajero, nombrePasajero);
    }

    @Transactional
    public String registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol("USER");
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        // Crear pasajero
        Pasajero pasajero = new Pasajero();
        pasajero.setNombreCompleto(request.getNombreCompleto());
        pasajero.setNumPasaporte(request.getNumPasaporte());
        pasajero.setFechaNac(request.getFechaNac().toLocalDate());
        pasajero.setNacionalidad(request.getNacionalidad());
        pasajero.setEmail(request.getEmail());
        pasajero.setNumTelefono(request.getNumTelefono());
        pasajero.setUsuario(usuario);
        pasajeroRepository.save(pasajero);

        return "Usuario registrado exitosamente";
    }
}
