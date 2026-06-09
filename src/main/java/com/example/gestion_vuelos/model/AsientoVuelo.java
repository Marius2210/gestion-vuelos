package com.example.gestion_vuelos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "asiento_vuelo", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_vuelo", "numero_asiento"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsientoVuelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAsientoVuelo;

    @ManyToOne
    @JoinColumn(name = "id_vuelo", nullable = false)
    private Vuelo vuelo;

    @Column(name = "numero_asiento", nullable = false, length = 5)
    private String numeroAsiento;

    @Column(name = "fila")
    private Integer fila;

    @Column(name = "letra", length = 1)
    private String letra;

    @Column(name = "tipo", length = 20)
    private String tipo; // VENTANA, PASILLO, CENTRO

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AsientoEstado estado = AsientoEstado.DISPONIBLE;

    @OneToOne
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;
}
