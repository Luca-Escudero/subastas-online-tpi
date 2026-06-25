package com.subastas.tpi.controller;

import com.subastas.tpi.dto.response.NotificacionResponseDTO;
import com.subastas.tpi.entity.Usuario;
import com.subastas.tpi.repository.UsuarioRepository;
import com.subastas.tpi.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final UsuarioRepository usuarioRepository;

    public NotificacionController(NotificacionService notificacionService, UsuarioRepository usuarioRepository) {
        this.notificacionService = notificacionService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> obtenerMisNotificaciones() {
        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        
        Usuario usuario = usuarioRepository.findByEmail(emailAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado en el sistema."));

        List<NotificacionResponseDTO> response = notificacionService.obtenerMisNotificaciones(usuario.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/test")
    public ResponseEntity<String> crearNotificacionTest(@RequestParam Long usuarioId, @RequestBody String mensaje) {
        Usuario destinatario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("El usuario destino no existe."));
        
        notificacionService.crearNotificacion(destinatario, mensaje);
        return ResponseEntity.ok("Notificación de prueba generada correctamente.");
    }
}