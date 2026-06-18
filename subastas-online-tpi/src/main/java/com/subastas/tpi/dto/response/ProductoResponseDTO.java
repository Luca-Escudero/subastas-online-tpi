package com.subastas.tpi.dto.response;

public record ProductoResponseDTO(
    Long id,
    String nombre,
    String descripcion,
    String imagenUrl,
    Long categoriaId,
    String categoriaNombre,
    Long vendedorId,
    String vendedorNombre
){}