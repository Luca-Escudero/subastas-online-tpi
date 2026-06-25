package com.subastas.tpi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.subastas.tpi.entity.Categoria;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
        Optional<Categoria> findById(Long id);
}
