package com.subastas.tpi.repository;

import com.subastas.tpi.entity.HistorialEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {
    List<HistorialEstado> findBySubastaIdOrderByFechaAsc(Long subastaId);
}
