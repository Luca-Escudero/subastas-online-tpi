package com.subastas.tpi.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PujaResponseDTO (
     Long id,
     Long subastaId,
     Long usuarioId,
     BigDecimal monto,
     LocalDateTime fechaPuja
){}