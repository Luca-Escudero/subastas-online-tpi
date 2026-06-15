package com.subastas.tpi.dto.response;

public record UsuarioResponseDTO(
    
    Integer id,
    String nombre,
    String apellido,
    String email,
    String telefono,
    Boolean activo
) {}