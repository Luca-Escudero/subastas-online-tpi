package com.subastas.tpi.controller;

import com.subastas.tpi.dto.request.SubastaRequestDTO;
import com.subastas.tpi.dto.response.SubastaResponseDTO;
import com.subastas.tpi.service.SubastaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subastas")
public class SubastaController {

    private final SubastaService subastaService;

    public SubastaController(SubastaService subastaService) {this.subastaService = subastaService;}

    @PostMapping
    public ResponseEntity<SubastaResponseDTO> crear (@Valid @RequestBody SubastaRequestDTO request){
        SubastaResponseDTO response = subastaService.crearSubasta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/publicacion")
    public ResponseEntity<Void> publicar (@PathVariable Long id){
        subastaService.publicarSubasta(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SubastaResponseDTO>> obtenerTodos(){
        return ResponseEntity.ok(subastaService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubastaResponseDTO> obtenerPorId(@PathVariable Long id){
        SubastaResponseDTO response = subastaService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        subastaService.eliminarSubasta(id);
        return ResponseEntity.noContent().build();
    }
}
