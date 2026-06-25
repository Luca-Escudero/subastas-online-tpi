package com.subastas.tpi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.subastas.tpi.entity.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
