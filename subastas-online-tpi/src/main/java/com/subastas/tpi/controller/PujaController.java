package com.subastas.tpi.controller;

import com.subastas.tpi.dto.response.PujaResponseDTO;
import com.subastas.tpi.service.PujaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/pujas")
public class PujaController {

    private final PujaService pujaService;

    public PujaController(PujaService pujaService) {
        this.pujaService = pujaService;
    }

    @PostMapping
    public ResponseEntity<PujaResponseDTO> crearPuja(@RequestBody PujaResponseDTO dto, Principal principal) {
        // Obtenés el ID del usuario autenticado desde el contexto de seguridad
        Long usuarioId = Long.valueOf(principal.getName()); 
        
        PujaResponseDTO respuesta = pujaService.realizarPuja(dto, usuarioId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}