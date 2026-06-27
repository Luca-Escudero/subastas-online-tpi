package com.subastas.tpi.dto.response;

import com.subastas.tpi.entity.EstadoSubasta;

import java.time.LocalDateTime;

public record HistorialEstadoResponseDTO (
        EstadoSubasta estado,
        LocalDateTime fecha,
        Long usuarioId,
        String detalle
) {}
