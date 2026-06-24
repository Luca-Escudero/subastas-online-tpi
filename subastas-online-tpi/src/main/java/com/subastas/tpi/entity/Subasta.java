package com.subastas.tpi.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subastas")

public class Subasta {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Producto producto;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaCierre;

    private LocalDateTime fechaAdjudicacion;

    private BigDecimal precioInicial;

    @Enumerated(EnumType.STRING)
    private EstadoSubasta estado;

    private BigDecimal montoActual;

    private BigDecimal incrementoMinimo;

    @ManyToOne
    private Usuario usuarioGanador;

    //private Puja pujaGanadora;

    @Version
    private Integer version;
}
