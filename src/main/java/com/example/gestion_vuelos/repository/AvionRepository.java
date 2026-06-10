package com.example.gestion_vuelos.repository;

import com.example.gestion_vuelos.model.Avion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AvionRepository extends JpaRepository<Avion, Integer> {
    List<Avion> findByAerolineaIdAerolinea(Integer idAerolinea);
}