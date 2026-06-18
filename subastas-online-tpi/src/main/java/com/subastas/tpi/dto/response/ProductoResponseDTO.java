package com.subastas.tpi.dto.response;

public record ProductoResponseDTO(
    Integer id,
    String nombre,
    String descripcion,
    String imagenUrl,
    Integer categoriaId,
    String categoriaNombre,
    Integer vendedorId,
    String vendedorNombre
){} 