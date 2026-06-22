package com.subastas.tpi.repository;

import com.subastas.tpi.entity.Subasta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubastaRepository extends JpaRepository<Subasta, Long> {
}
