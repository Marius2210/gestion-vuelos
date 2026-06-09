package com.example.gestion_vuelos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tripulacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tripulacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTripulante;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String cargo;

    @ManyToOne
    @JoinColumn(name = "id_aerolinea", nullable = false)
    @JsonIgnore
    private Aerolinea aerolinea;
}
