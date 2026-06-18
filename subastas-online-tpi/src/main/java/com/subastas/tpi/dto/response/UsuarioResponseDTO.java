package com.subastas.tpi.dto.response;

public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String apellido,
        String email,
        String telefono,
        Boolean activo
) {}