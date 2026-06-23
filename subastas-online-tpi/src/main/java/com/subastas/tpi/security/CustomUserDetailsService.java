package com.subastas.tpi.security;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.subastas.tpi.entity.Usuario;
import com.subastas.tpi.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Buscamos el usuario por su email
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // 2. Si está suspendido, le cortamos el paso acá mismo
        if (!usuario.getActivo()) {
            throw new RuntimeException("El usuario se encuentra bloqueado por un administrador.");
        }

        // 3. Convertimos tus Roles de MySQL al formato que entiende Spring Security ("ROLE_USER")
        var authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
                .collect(Collectors.toList());

        // 4. Devolvemos el objeto User propio de Spring Security
        return new User(usuario.getEmail(), usuario.getPassword(), authorities);
    }   

}
