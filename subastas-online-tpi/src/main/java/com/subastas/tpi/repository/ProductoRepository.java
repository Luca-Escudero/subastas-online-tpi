package com.subastas.tpi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.subastas.tpi.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

}
