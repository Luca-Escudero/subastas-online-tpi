package com.subastas.tpi.controller;

import com.subastas.tpi.dto.request.SubastaRequestDTO;
import com.subastas.tpi.dto.response.SubastaResponseDTO;
import com.subastas.tpi.service.SubastaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.security.Principal;
/**
     * java.security.Principal representa la identidad (usuario) 
     * que ha sido autenticada en el sistema. Se usa para obtener el nombre 
     * del usuario actual mediante principal.getName().
     */

@RestController
@RequestMapping("/api/subastas")
public class SubastaController {

    private final SubastaService subastaService;

    public SubastaController(SubastaService subastaService) {this.subastaService = subastaService;}

    @PostMapping
    public ResponseEntity<SubastaResponseDTO> crear (@Valid @RequestBody SubastaRequestDTO request, Principal principal){
        SubastaResponseDTO response = subastaService.crearSubasta(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping({"/{id}/publicar", "/{id}/publicacion"})
    public ResponseEntity<SubastaResponseDTO> publicar (@PathVariable Long id, Principal principal){
        SubastaResponseDTO response = subastaService.publicarSubasta(id, principal.getName());
        return ResponseEntity.ok(response);
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
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Principal principal){
        subastaService.eliminarSubasta(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

   
}
