package com.subastas.tpi.dto.response;

import java.time.LocalDateTime;

public record NotificacionResponseDTO(
    Long id,
    String mensaje,
    LocalDateTime fechaEnvio,
    Long destinatarioId,
    String destinatarioNombre
) {}