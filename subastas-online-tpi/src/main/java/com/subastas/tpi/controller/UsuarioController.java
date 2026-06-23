package com.subastas.tpi.controller;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.subastas.tpi.dto.request.UsuarioRegistroDTO;
import com.subastas.tpi.dto.response.UsuarioResponseDTO;
import com.subastas.tpi.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // PÚBLICO: Registro
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@Valid @RequestBody UsuarioRegistroDTO dto) {
        UsuarioResponseDTO response = usuarioService.registrarUsuario(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // SOLO ADMIN: Listar todos
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    // SOLO ADMIN: Bloquear/Desbloquear
    @PutMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponseDTO> cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        UsuarioResponseDTO response = usuarioService.cambiarEstadoUsuario(id, activo);
        return ResponseEntity.ok(response);
    }
    
}
