package com.subastas.tpi.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime fechaPuja;
}