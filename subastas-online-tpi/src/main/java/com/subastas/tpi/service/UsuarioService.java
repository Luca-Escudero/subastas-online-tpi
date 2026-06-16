package com.subastas.tpi.service;

import org.springframework.stereotype.Service;

import com.subastas.tpi.dto.request.UsuarioRegistroDTO;
import com.subastas.tpi.dto.response.UsuarioResponseDTO;
import com.subastas.tpi.entity.Rol;
import com.subastas.tpi.entity.Usuario;
import com.subastas.tpi.repository.RolRepository;
import com.subastas.tpi.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;


    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    public UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO dto) {
        // 1. Controlar que el email no esté duplicado 
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Traspaso de datos: Del Record (inmutable) a la Entidad (JPA)
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.nombre());
        nuevoUsuario.setApellido(dto.apellido());
        nuevoUsuario.setEmail(dto.email());
        nuevoUsuario.setPassword(dto.password()); // Falta encriptar con Spring Security
        nuevoUsuario.setTelefono(dto.telefono());
        nuevoUsuario.setActivo(true);

        Rol rolUser = rolRepository.findByNombre("USER")
                .orElseThrow(() -> new RuntimeException("Error crítico: El rol 'USER' no existe en la base de datos"));
                
        Rol rolSeller = rolRepository.findByNombre("SELLER")
                .orElseThrow(() -> new RuntimeException("Error crítico: El rol 'SELLER' no existe en la base de datos"));
        
        // Le asignamos los DOS roles por defecto al usuario
        nuevoUsuario.getRoles().add(rolUser);
        nuevoUsuario.getRoles().add(rolSeller);
       
        //  Impactar en MySQL
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // Transformar el resultado en el DTO de salida
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
