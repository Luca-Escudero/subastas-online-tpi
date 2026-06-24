package com.subastas.tpi.dto.response;

import com.subastas.tpi.entity.EstadoSubasta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SubastaResponseDTO (
    Long id,
    Long productoId,
    String productoNombre,
    LocalDateTime fechaInicio,
    LocalDateTime fechaCierre,
    LocalDateTime fechaAdjudicacion,
    BigDecimal precioInicial,
    EstadoSubasta estado,
    BigDecimal montoActual,
    BigDecimal incrementoMinimo,
    Long usuarioGanadorId,
    String usuarioGanadorNombre
){}
