package com.subastas.tpi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.subastas.tpi.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    
    Optional<Categoria> findById(Integer id);


}
