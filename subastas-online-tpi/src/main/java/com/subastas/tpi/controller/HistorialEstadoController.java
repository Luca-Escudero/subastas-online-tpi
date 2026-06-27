package com.subastas.tpi.controller;

import com.subastas.tpi.dto.response.HistorialEstadoResponseDTO;
import com.subastas.tpi.entity.HistorialEstado;
import com.subastas.tpi.service.HistorialEstadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subastas")
public class HistorialEstadoController {

    private final HistorialEstadoService historialEstadoService;

    public HistorialEstadoController(HistorialEstadoService historialEstadoService){
        this.historialEstadoService = historialEstadoService;
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialEstadoResponseDTO>> obtenerHistorialPorId(@PathVariable Long id){
        return ResponseEntity.ok(historialEstadoService.obtenerHistorialPorSubasta(id));
    }

}
