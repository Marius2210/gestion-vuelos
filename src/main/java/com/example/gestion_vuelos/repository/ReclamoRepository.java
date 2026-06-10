package com.example.gestion_vuelos.repository;

import com.example.gestion_vuelos.model.Reclamo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReclamoRepository extends JpaRepository<Reclamo, Integer> {
    List<Reclamo> findByReservaIdReserva(Integer idReserva);
}
