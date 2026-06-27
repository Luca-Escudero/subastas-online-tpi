package com.subastas.tpi.dto.request;

import com.subastas.tpi.validation.FechaCierreValida;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SubastaRequestDTO(

    @NotNull(message = "El ID del producto es obligatorio")
    Long productoId,

    @Future(message = "La fecha de inicio no puede ser pasada o igual a hoy")
    LocalDateTime fechaInicio,

    @FechaCierreValida
    LocalDateTime fechaCierre,

    @NotNull(message = "Campo obligatorio")
    @Digits(integer = 7, fraction = 2,
            message = "Valor máximo(parte entera): 9.999.999\nValor máximo(parte decimal): 99")
    BigDecimal precioInicial,

    @NotNull(message = "Campo obligatorio")
    @Digits(integer = 6, fraction = 2,
            message = "Valor máximo(parte entera): 999.999\nValor máximo(parte decimal): 99")
    BigDecimal incrementoMinimo
)
{}
