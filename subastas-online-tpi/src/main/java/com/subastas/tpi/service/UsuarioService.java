package com.subastas.tpi.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.subastas.tpi.dto.request.UsuarioRegistroDTO;
import com.subastas.tpi.dto.response.UsuarioResponseDTO;
import com.subastas.tpi.entity.Rol;
import com.subastas.tpi.entity.Usuario;
import com.subastas.tpi.repository.RolRepository;
import com.subastas.tpi.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO dto) {
        // Controlar que el email no esté duplicado
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        //  Traspaso de datos: Del Record (inmutable) a la Entidad (JPA)
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.nombre());
        nuevoUsuario.setApellido(dto.apellido());
        nuevoUsuario.setEmail(dto.email());
        nuevoUsuario.setPassword(passwordEncoder.encode(dto.password()));
        nuevoUsuario.setTelefono(dto.telefono());
        nuevoUsuario.setActivo(true);

        Rol rolUser = rolRepository.findByNombre("USER")
                .orElseThrow(() -> new RuntimeException("Error Critico: El rol 'USER' no exixte en la base de datos"));

        Rol rolSeller = rolRepository.findByNombre("SELLER")
                .orElseThrow(() -> new RuntimeException("Error Critico: El rol 'SELLER' no exixte en la base de datos"));

        // Le asigno los 2 roles por defecto
        nuevoUsuario.getRoles().add(rolUser);
        nuevoUsuario.getRoles().add(rolSeller);

        //  Impactar en MySQL
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        //  Transformar el resultado en el DTO de salida
        return new UsuarioResponseDTO(
                usuarioGuardado.getId(),
                usuarioGuardado.getNombre(),
                usuarioGuardado.getApellido(),
                usuarioGuardado.getEmail(),
                usuarioGuardado.getTelefono(),
                usuarioGuardado.getActivo(),
                usuarioGuardado.getRoles().stream().map(Rol::getNombre).toList());
                

    }

    // Listar todos los usuarios del sistema mapeados a DTO
    public List<UsuarioResponseDTO> obtenerTodos() {
        return usuarioRepository.findAll().stream()
            .map(u -> new UsuarioResponseDTO(
                u.getId(), u.getNombre(), u.getApellido(), 
                u.getEmail(), u.getTelefono(), u.getActivo(),
                u.getRoles().stream().map(Rol::getNombre).toList()
            ))
            .toList();
    }

    @Transactional
    public UsuarioResponseDTO cambiarEstadoUsuario(Long id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el ID: " + id));
        
        usuario.setActivo(activo);
        Usuario usuarioActualizado = usuarioRepository.save(usuario); 
        
        return new UsuarioResponseDTO(
                usuarioActualizado.getId(), usuarioActualizado.getNombre(), 
                usuarioActualizado.getApellido(), usuarioActualizado.getEmail(), 
                usuarioActualizado.getTelefono(), usuarioActualizado.getActivo(),
                usuarioActualizado.getRoles().stream().map(Rol::getNombre).toList());

    }

    public UsuarioResponseDTO obtenerPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el email: " + email));
        return new UsuarioResponseDTO(
                usuario.getId(), usuario.getNombre(), 
                usuario.getApellido(), usuario.getEmail(), 
                usuario.getTelefono(), usuario.getActivo(),
                usuario.getRoles().stream().map(Rol::getNombre).toList());
    }
}
