package com.subastas.tpi.dto.response;

public record ErrorResponseDTO(
    String mensaje,
    int status
) {}
