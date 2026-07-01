package com.subastas.tpi.service;

import com.subastas.tpi.dto.request.PujaRequestDTO;
import com.subastas.tpi.dto.response.PujaResponseDTO;
import com.subastas.tpi.entity.EstadoSubasta;
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
import java.time.ZoneOffset;

@Service
@Transactional
public class PujaService {

    private final PujaRepository pujaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SubastaRepository subastaRepository;
    private final NotificacionService notificacionService;
    private final HistorialEstadoService historialEstadoService;

    public PujaService(PujaRepository pujaRepository, 
                       UsuarioRepository usuarioRepository,
                       SubastaRepository subastaRepository, 
                       NotificacionService notificacionService,
                       HistorialEstadoService historialEstadoService) {
        this.pujaRepository = pujaRepository;
        this.usuarioRepository = usuarioRepository;
        this.subastaRepository = subastaRepository;
        this.notificacionService = notificacionService;
        this.historialEstadoService = historialEstadoService;
    }

    @Transactional
    public PujaResponseDTO realizarPuja(PujaRequestDTO dto, String emailUsuario) {
        LocalDateTime ahora = LocalDateTime.now(ZoneOffset.UTC);

        // 1. Validar existencia de la subasta
        Subasta subasta = subastaRepository.findById(dto.subastaId())
                .orElseThrow(() -> new RuntimeException("La subasta no existe."));

        // 2. Validar que la subasta esté en un estado que permita recibir pujas (PUBLICADA o ACTIVA)
        if (subasta.getEstado() != EstadoSubasta.PUBLICADA && subasta.getEstado() != EstadoSubasta.ACTIVA) {
            throw new RuntimeException("La subasta no está activa para recibir pujas (estado actual: " + subasta.getEstado() + ").");
        }

        // 3. Validar rango de tiempo de la subasta
        if (ahora.isBefore(subasta.getFechaInicio())) {
            throw new RuntimeException("La subasta aún no ha comenzado.");
        }
        if (ahora.isAfter(subasta.getFechaCierre())) {
            throw new RuntimeException("La subasta ya ha finalizado.");
        }

        // 4. Validar existencia del usuario que puja por su email
        Usuario usuarioQuePuja = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("El usuario no existe en el sistema."));

        // 5. Validar que el vendedor no pueda pujar en su propia subasta
        if (subasta.getProducto().getVendedor().getEmail().equals(emailUsuario)) {
            throw new RuntimeException("El vendedor no puede pujar en su propia subasta.");
        }

        // 6. Validar monto ofertado respetando el incremento mínimo
        BigDecimal montoOfertado = dto.monto();
        BigDecimal montoMinimoRequerido = subasta.getMontoActual();
        if (subasta.getUsuarioGanador() != null) {
            montoMinimoRequerido = montoMinimoRequerido.add(subasta.getIncrementoMinimo());
        }
        if (montoOfertado == null || montoOfertado.compareTo(montoMinimoRequerido) < 0) {
            throw new RuntimeException("El monto de la puja debe ser de al menos " + montoMinimoRequerido + " (respetando el incremento mínimo).");
        }

        // 7. Guardar el usuario que va perdiendo antes de actualizar (para notificarlo)
        Usuario usuarioAnteriorGanador = subasta.getUsuarioGanador();

        // 8. Mapear y registrar la nueva Puja en la base de datos
        Puja nuevaPuja = new Puja();
        nuevaPuja.setSubasta(subasta);
        nuevaPuja.setUsuario(usuarioQuePuja);
        nuevaPuja.setMonto(montoOfertado);
        pujaRepository.save(nuevaPuja);

        // 9. Actualizar el monto actual y el ganador de la subasta
        subasta.setMontoActual(montoOfertado);
        subasta.setUsuarioGanador(usuarioQuePuja);

        // 10. Si la subasta estaba PUBLICADA, pasa a estar ACTIVA al recibir su primera puja
        if (subasta.getEstado() == EstadoSubasta.PUBLICADA) {
            subasta.setEstado(EstadoSubasta.ACTIVA);
            historialEstadoService.registrarCambioEstado(
                    subasta,
                    EstadoSubasta.ACTIVA,
                    usuarioQuePuja,
                    "Subasta activada al recibir su primera puja"
            );
        }

        // 11. Aplicar regla de Anti-Sniping (Si puja en los últimos 5 minutos, se extiende 5 más)
        if (ahora.isAfter(subasta.getFechaCierre().minusMinutes(5))) {
            subasta.setFechaCierre(subasta.getFechaCierre().plusMinutes(5));
        }

        subastaRepository.save(subasta);

        // 12. Notificar al usuario que acaba de ser superado (si es que había uno)
        // if (usuarioAnteriorGanador != null && !usuarioAnteriorGanador.getId().equals(usuarioQuePuja.getId())) {
        //     notificacionService.enviarAvisoSuperado(usuarioAnteriorGanador, subasta, montoOfertado);
        // }

        // 13. Mapear y retornar el DTO de respuesta
        PujaResponseDTO responseDTO = new PujaResponseDTO();
        responseDTO.setId(nuevaPuja.getId());
        responseDTO.setSubastaId(subasta.getId());
        responseDTO.setUsuarioId(usuarioQuePuja.getId());
        responseDTO.setMonto(nuevaPuja.getMonto());
        responseDTO.setFechaPuja(nuevaPuja.getFechaPuja());

        return responseDTO;
    }
}