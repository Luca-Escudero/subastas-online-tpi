package com.subastas.tpi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRegistroDTO(
    /* Implemente 'record' en lugar de clases con Lombok (@Data/@Getter) para el manejo de DTOs. 
    1. Seguridad (Inmutabilidad): El record sella el payload. No existen los Setters. 
    Garantiza que el JSON que entra por el Controller llegue intacto al Service sin 
    que nadie pise los datos por error en el medio de la ejecución.
    2. Sintaxis: Los getters se llaman directamente por el nombre de la variable. 
    Ej: usar dto.email() en vez de dto.getEmail().
    */
    
    @NotBlank(message = "El nombre es obligatorio")
    String nombre,
    
    @NotBlank(message = "El apellido es obligatorio")
    String apellido,
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    String email,
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    String password,
    
    @NotBlank(message = "El teléfono es obligatorio")
    String telefono

) {}
