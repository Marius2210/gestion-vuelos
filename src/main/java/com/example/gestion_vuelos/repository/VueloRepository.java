package com.example.gestion_vuelos.repository;

import com.example.gestion_vuelos.model.Vuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VueloRepository extends JpaRepository<Vuelo, Integer> {
    List<Vuelo> findByAvionIdAvion(Integer idAvion);
    Optional<Vuelo> findByNumeroVuelo(String numeroVuelo);
    boolean existsByNumeroVuelo(String numeroVuelo);

    @Query("SELECT v FROM Vuelo v JOIN v.tripulantes t WHERE t.idTripulante = :idTripulante")
    List<Vuelo> findVuelosByTripulanteId(@Param("idTripulante") Integer idTripulante);

    @Query("SELECT v FROM Vuelo v WHERE v.origen = :origen AND v.destino = :destino " +
            "AND DATE(v.fechaSalida) = DATE(:fechaSalida) AND v.estado != 'C'")
    List<Vuelo> findVuelosDisponibles(@Param("origen") String origen,
                                      @Param("destino") String destino,
                                      @Param("fechaSalida") LocalDateTime fechaSalida);
}
