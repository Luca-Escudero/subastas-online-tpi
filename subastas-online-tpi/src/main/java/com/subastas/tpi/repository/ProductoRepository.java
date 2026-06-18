package com.subastas.tpi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.subastas.tpi.entity.Producto;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

}
