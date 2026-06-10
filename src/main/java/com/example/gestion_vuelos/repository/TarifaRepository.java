package com.example.gestion_vuelos.repository;

import com.example.gestion_vuelos.model.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarifaRepository extends JpaRepository<Tarifa, Integer> {
    void deleteByVueloIdVuelo(Integer idVuelo);
}