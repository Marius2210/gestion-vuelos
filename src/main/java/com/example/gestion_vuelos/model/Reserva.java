package com.example.gestion_vuelos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReserva;

    @Column(nullable = false, unique = true, length = 8)
    private String codigoReserva;

    @ManyToOne
    @JoinColumn(name = "id_vuelo", nullable = false)
    private Vuelo vuelo;

    @ManyToOne
    @JoinColumn(name = "id_pasajero", nullable = false)
    private Pasajero pasajero;

    @Column(nullable = false, length = 15)
    private String estadoReserva = "PEN";

    @Column(nullable = false)
    private LocalDateTime fechaReserva = LocalDateTime.now();

    private String asientoPreferencia;

    private BigDecimal precioTotal;

    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL)
    private Pago pago;

    @OneToMany(mappedBy = "reserva")
    private List<Reclamo> reclamos;
}
