package com.subastas.tpi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductoRequestDTO(
    
    @NotBlank(message = "El nombre es obligatorio y no puede estar vacío")
    String nombre,
    
    @NotBlank(message = "La descripción es obligatoria")
    String descripcion,
    
    String imagenUrl, 
    
    @NotNull(message = "El ID de la categoría es obligatorio")
    Long categoriaId
){}