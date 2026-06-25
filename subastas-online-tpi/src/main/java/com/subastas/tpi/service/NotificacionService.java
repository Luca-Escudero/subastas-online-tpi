package com.subastas.tpi.service;

import com.subastas.tpi.dto.response.NotificacionResponseDTO;
import com.subastas.tpi.entity.Notificacion;
import com.subastas.tpi.entity.Usuario;
import com.subastas.tpi.repository.NotificacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @Transactional
    public void crearNotificacion(Usuario destinatario, String mensaje) {
        Notificacion nueva = new Notificacion();
        nueva.setDestinatario(destinatario);
        nueva.setMensaje(mensaje);
        
        notificacionRepository.save(nueva);
    }

    // mapeos
    public List<NotificacionResponseDTO> obtenerMisNotificaciones(Long usuarioId) {
        return notificacionRepository.findByDestinatarioIdOrderByFechaEnvioDesc(usuarioId)
                .stream()
                .map(this::toResponseFromEntity)
                .toList();
    }

    private NotificacionResponseDTO toResponseFromEntity(Notificacion notificacion) {
        return new NotificacionResponseDTO(
                notificacion.getId(),
                notificacion.getMensaje(),
                notificacion.getFechaEnvio(),
                notificacion.getDestinatario().getId(),
                notificacion.getDestinatario().getNombre()
        );
    }
}