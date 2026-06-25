package com.subastas.tpi.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class PujaResponseDTO {
    private Long id;
    private Long subastaId;
    private Long usuarioId;
    private BigDecimal monto;
    private LocalDateTime fechaPuja;
}