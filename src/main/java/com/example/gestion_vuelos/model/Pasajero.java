package com.example.gestion_vuelos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "pasajero")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pasajero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPasajero;

    @Column(nullable = false, length = 150)
    private String nombreCompleto;

    @Column(nullable = false, unique = true, length = 20)
    private String numPasaporte;

    @Column(nullable = false)
    private LocalDate fechaNac;

    @Column(nullable = false, length = 20)
    private String nacionalidad;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 10)
    private String numTelefono;

    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}
