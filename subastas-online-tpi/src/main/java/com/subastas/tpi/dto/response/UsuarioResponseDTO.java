package com.subastas.tpi.dto.response;

import java.util.List;

public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String apellido,
        String email,
        String telefono,
        Boolean activo,
        List<String> roles
) {}