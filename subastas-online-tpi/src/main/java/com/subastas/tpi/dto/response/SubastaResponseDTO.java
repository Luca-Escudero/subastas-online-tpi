package com.subastas.tpi.dto.response;

import com.subastas.tpi.entity.EstadoSubasta;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SubastaResponseDTO (
    Long id,
    Long productoId,
    String productoNombre,
     /**
     * Especifica el formato de serialización/deserialización JSON para este campo.
     * Formato ISO-8601: Año-Mes-DíaTHora:Minuto:SegundoZ (ej. 2026-06-30T22:36:45Z).
     */

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    LocalDateTime fechaInicio,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    LocalDateTime fechaCierre,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    LocalDateTime fechaAdjudicacion,

    BigDecimal precioInicial,
    EstadoSubasta estado,
    BigDecimal montoActual,
    BigDecimal incrementoMinimo,
    Long usuarioGanadorId,
    String usuarioGanadorNombre,
    String usuarioGanadorEmail
){}
