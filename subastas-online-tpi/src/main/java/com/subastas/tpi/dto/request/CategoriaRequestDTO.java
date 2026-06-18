package com.subastas.tpi.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequestDTO (

        @NotBlank(message = "El nombre es obligatorio y no puede estar vacío")
        String nombre
){}
