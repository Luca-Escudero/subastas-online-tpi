package com.subastas.tpi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "historial_estados")
public class HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Subasta subasta;

    @Enumerated(EnumType.STRING)
    private EstadoSubasta nombreEstado;

    private LocalDateTime fecha;

    @ManyToOne
    private Usuario usuario;

    @Column(length = 1000)
    private String detalle;
}
