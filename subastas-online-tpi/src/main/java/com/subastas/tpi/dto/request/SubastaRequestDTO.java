package com.subastas.tpi.dto.request;

import com.subastas.tpi.validation.FechaCierreValida;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SubastaRequestDTO(

    @NotNull(message = "El ID del producto es obligatorio")
    Long productoId,

    @NotBlank(message = "La fecha de inicio es obligatoria y no puede estar vacía")
    @Future(message = "La fecha de inicio no puede ser pasada o igual a hoy")
    LocalDateTime fechaInicio,

    @NotBlank(message = "La fecha de cierre es obligatoria y no puede estar vacía")
    @FechaCierreValida
    LocalDateTime fechaCierre,

    @NotBlank(message = "El precio inicial es obligatorio y no puede estar vacío")
    @Digits(integer = 7, fraction = 2,
            message = "Valor máximo(parte entera): 9.999.999\nValor máximo(parte decimal): 99")
    BigDecimal precioInicial,

    @NotBlank(message = "El incremento mínimo es obligatorio y no puede estar vacío")
    @Digits(integer = 6, fraction = 2,
            message = "Valor máximo(parte entera): 999.999\nValor máximo(parte decimal): 99")
    BigDecimal incrementoMinimo
)
{}
