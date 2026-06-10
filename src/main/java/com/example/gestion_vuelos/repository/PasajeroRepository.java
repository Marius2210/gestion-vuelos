package com.example.gestion_vuelos.repository;

import com.example.gestion_vuelos.model.Pasajero;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasajeroRepository extends JpaRepository<Pasajero, Integer> {
    Optional<Pasajero> findByNumPasaporte(String numPasaporte);
    Optional<Pasajero> findByEmail(String email);
}
