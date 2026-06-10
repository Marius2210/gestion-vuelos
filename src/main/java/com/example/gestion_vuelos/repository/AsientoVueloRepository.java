package com.example.gestion_vuelos.repository;

import com.example.gestion_vuelos.model.AsientoEstado;
import com.example.gestion_vuelos.model.AsientoVuelo;
import com.example.gestion_vuelos.model.Vuelo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AsientoVueloRepository extends JpaRepository<AsientoVuelo, Integer> {

    List<AsientoVuelo> findByVuelo(Vuelo vuelo);

    List<AsientoVuelo> findByVueloAndEstado(Vuelo vuelo, AsientoEstado estado);

    Optional<AsientoVuelo> findByVueloAndNumeroAsiento(Vuelo vuelo, String numeroAsiento);

    long countByVueloAndEstado(Vuelo vuelo, AsientoEstado estado);

    boolean existsByVueloAndNumeroAsientoAndEstadoNot(Vuelo vuelo, String numeroAsiento, AsientoEstado estado);

    @Modifying
    @Query("UPDATE AsientoVuelo a SET a.estado = :nuevoEstado WHERE a.vuelo = :vuelo AND a.numeroAsiento = :numeroAsiento")
    int actualizarEstadoAsiento(@Param("vuelo") Vuelo vuelo,
                                @Param("numeroAsiento") String numeroAsiento,
                                @Param("nuevoEstado") AsientoEstado nuevoEstado);

    // Obtener asientos disponibles con sus tipos
    @Query("SELECT a FROM AsientoVuelo a WHERE a.vuelo = :vuelo AND a.estado = :estado ORDER BY a.fila, a.letra")
    List<AsientoVuelo> findAsientosDisponiblesOrdenados(@Param("vuelo") Vuelo vuelo,
                                                        @Param("estado") AsientoEstado estado);

    // Eliminar asientos por ID de vuelo
    @Modifying
    @Transactional
    @Query("DELETE FROM AsientoVuelo a WHERE a.vuelo.idVuelo = :idVuelo")
    void deleteByVueloId(@Param("idVuelo") Integer idVuelo);
}