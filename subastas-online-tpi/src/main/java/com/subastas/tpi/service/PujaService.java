package com.subastas.tpi.service;

import com.subastas.tpi.dto.response.PujaResponseDTO;
import com.subastas.tpi.entity.Puja;
import com.subastas.tpi.entity.Subasta;
import com.subastas.tpi.entity.Usuario;
import com.subastas.tpi.repository.PujaRepository;
import com.subastas.tpi.repository.SubastaRepository;
import com.subastas.tpi.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class PujaService {

    private final PujaRepository pujaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SubastaRepository subastaRepository;
    private final NotificacionService notificacionService;

    public PujaService(PujaRepository pujaRepository, 
                       UsuarioRepository usuarioRepository,
                       SubastaRepository subastaRepository, 
                       NotificacionService notificacionService) {
        this.pujaRepository = pujaRepository;
        this.usuarioRepository = usuarioRepository;
        this.subastaRepository = subastaRepository;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public PujaResponseDTO realizarPuja(PujaResponseDTO dto, Long usuarioId) {
        LocalDateTime ahora = LocalDateTime.now();

        // 1. Validar existencia de la subasta
        Subasta subasta = subastaRepository.findById(dto.getSubastaId())
                .orElseThrow(() -> new RuntimeException("La subasta no existe."));

        // 2. Validar que la subasta no haya terminado por tiempo
        if (ahora.isAfter(subasta.getFechaCierre())) {
            throw new RuntimeException("La subasta ya ha finalizado.");
        }

        // 3. Validar el monto ofertado usando compareTo
        BigDecimal montoOfertado = dto.getMonto();
        if (montoOfertado == null || montoOfertado.compareTo(subasta.getMontoActual()) <= 0) {
            throw new RuntimeException("El monto de la puja debe ser mayor al monto actual.");
        }

        // 4. Validar existencia del usuario que puja
        Usuario usuarioQuePuja = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("El usuario no existe."));

        // 5. Guardar el usuario que va perdiendo antes de actualizar (para notificarlo)
        Usuario usuarioAnteriorGanador = subasta.getUsuarioGanador();

        // 6. Mapear y registrar la nueva Puja en la base de datos
        Puja nuevaPuja = new Puja();
        nuevaPuja.setSubasta(subasta);
        nuevaPuja.setUsuario(usuarioQuePuja);
        nuevaPuja.setMonto(montoOfertado);
        pujaRepository.save(nuevaPuja);

        // 7. Actualizar el estado de la subasta con el nuevo puntero
        subasta.setMontoActual(montoOfertado);
        subasta.setUsuarioGanador(usuarioQuePuja);

        // 8. Aplicar regla de Anti-Sniping (Si puja en los últimos 5 minutos, se extiende 5 más)
        if (ahora.isAfter(subasta.getFechaCierre().minusMinutes(5))) {
            subasta.setFechaCierre(subasta.getFechaCierre().plusMinutes(5));
        }

        subastaRepository.save(subasta);

        // 9. Notificar al usuario que acaba de ser superado (si es que había uno)
        // if (usuarioAnteriorGanador != null && !usuarioAnteriorGanador.getId().equals(usuarioId)) {
        //     notificacionService.enviarAvisoSuperado(usuarioAnteriorGanador, subasta, montoOfertado);
        // }

        // 10. Mapear y retornar el DTO de respuesta
        PujaResponseDTO responseDTO = new PujaResponseDTO();
        responseDTO.setId(nuevaPuja.getId());
        responseDTO.setSubastaId(subasta.getId());
        responseDTO.setUsuarioId(usuarioQuePuja.getId());
        responseDTO.setMonto(nuevaPuja.getMonto());
        responseDTO.setFechaPuja(nuevaPuja.getFechaPuja());

        return responseDTO;
    }
}