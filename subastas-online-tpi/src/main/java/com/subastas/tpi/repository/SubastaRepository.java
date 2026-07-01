package com.subastas.tpi.repository;

import com.subastas.tpi.entity.Subasta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubastaRepository extends JpaRepository<Subasta, Long> {
    boolean existsByProductoId(Long productoId);
}
