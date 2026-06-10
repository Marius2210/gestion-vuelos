package com.example.gestion_vuelos.repository;

import com.example.gestion_vuelos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Integer> {
    Optional<Pago> findByReservaIdReserva(Integer idReserva);
}
