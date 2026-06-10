package com.example.gestion_vuelos.repository;

import com.example.gestion_vuelos.model.Tripulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TripulacionRepository extends JpaRepository<Tripulacion, Integer> {
    List<Tripulacion> findByAerolineaIdAerolinea(Integer idAerolinea);
    List<Tripulacion> findByCargoIgnoreCase(String cargo);
    Optional<Tripulacion> findByNombreIgnoreCase(String nombre);
}
