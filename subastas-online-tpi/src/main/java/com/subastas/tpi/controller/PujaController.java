package com.subastas.tpi.controller;

import com.subastas.tpi.dto.request.PujaRequestDTO;
import com.subastas.tpi.dto.response.PujaResponseDTO;
import com.subastas.tpi.service.PujaService;
import jakarta.validation.Valid;
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
    public ResponseEntity<PujaResponseDTO> crearPuja(@Valid @RequestBody PujaRequestDTO dto, Principal principal) {
        PujaResponseDTO respuesta = pujaService.realizarPuja(dto, principal.getName());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}