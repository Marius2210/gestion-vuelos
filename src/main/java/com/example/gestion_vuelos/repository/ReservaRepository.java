package com.example.gestion_vuelos.repository;

import com.example.gestion_vuelos.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    List<Reserva> findByPasajeroIdPasajero(Integer idPasajero);
    Optional<Reserva> findByCodigoReserva(String codigoReserva);
    List<Reserva> findByVueloIdVuelo(Integer idVuelo);
}
