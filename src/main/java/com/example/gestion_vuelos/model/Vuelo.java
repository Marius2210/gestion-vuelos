package com.example.gestion_vuelos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vuelo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vuelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idVuelo;

    @Column(nullable = false, unique = true, length = 10)
    private String numeroVuelo;

    @Column(nullable = false, length = 100)
    private String origen;

    @Column(nullable = false, length = 100)
    private String destino;

    @Column(nullable = false)
    private LocalDateTime fechaSalida;

    @Column(nullable = false)
    private LocalDateTime fechaLlegada; // Duración implícita

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVuelo estado = EstadoVuelo.P;

    @ManyToOne
    @JoinColumn(name = "id_avion", nullable = false)
    private Avion avion;

    @OneToMany(mappedBy = "vuelo", cascade = CascadeType.ALL)
    private List<Tarifa> tarifas;

    @OneToMany(mappedBy = "vuelo")
    private List<Reserva> reservas;

    @ManyToMany
    @JoinTable(
            name = "vuelo_tripulacion",
            joinColumns = @JoinColumn(name = "id_vuelo"),
            inverseJoinColumns = @JoinColumn(name = "id_tripulante")
    )
    private List<Tripulacion> tripulantes;
}
