package com.example.gestion_vuelos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "aerolinea")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aerolinea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAerolinea;

    @Column(nullable = false, length = 100)
    private String nombreAerolinea;

    @Column(nullable = false, unique = true, length = 3)
    private String codigoIata;

    @OneToMany(mappedBy = "aerolinea", cascade = CascadeType.ALL)
    private List<Avion> aviones;

    @OneToMany(mappedBy = "aerolinea")
    private List<Tripulacion> tripulantes;
}
