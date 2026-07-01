package com.subastas.tpi.dto.response;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public record NotificacionResponseDTO(
    Long id,
    String mensaje,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    LocalDateTime fechaEnvio,
    Long destinatarioId,
    String destinatarioNombre
) {}