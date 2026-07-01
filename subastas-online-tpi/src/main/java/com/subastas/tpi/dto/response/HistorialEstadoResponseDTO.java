package com.subastas.tpi.dto.response;

import com.subastas.tpi.entity.EstadoSubasta;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record HistorialEstadoResponseDTO (
        EstadoSubasta estado,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime fecha,
        Long usuarioId,
        String detalle
) {}
