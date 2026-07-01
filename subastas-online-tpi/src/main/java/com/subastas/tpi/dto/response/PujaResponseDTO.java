package com.subastas.tpi.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public record PujaResponseDTO (
     Long id,
     Long subastaId,
     Long usuarioId,
     BigDecimal monto,
     @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
     LocalDateTime fechaPuja
){}
