package com.subastas.tpi.service;

import org.springframework.stereotype.Service;

import com.subastas.tpi.dto.request.UsuarioRegistroDTO;
import com.subastas.tpi.dto.response.UsuarioResponseDTO;
import com.subastas.tpi.entity.Usuario;
import com.subastas.tpi.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO dto) {
        // 1. Controlar que el email no esté duplicado 
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // 2. Traspaso de datos: Del Record (inmutable) a la Entidad (JPA)
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.nombre());
        nuevoUsuario.setApellido(dto.apellido());
        nuevoUsuario.setEmail(dto.email());
        nuevoUsuario.setPassword(dto.password()); // Falta encriptar con Spring Security
        nuevoUsuario.setTelefono(dto.telefono());
        nuevoUsuario.setActivo(true);

        // 3. Impactar en MySQL
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // 4. Transformar el resultado en el DTO de salida
        return new UsuarioResponseDTO(
            usuarioGuardado.getId(),
            usuarioGuardado.getNombre(),
            usuarioGuardado.getApellido(),
            usuarioGuardado.getEmail(),
            usuarioGuardado.getTelefono(),
            usuarioGuardado.getActivo()
        );
    }
}
