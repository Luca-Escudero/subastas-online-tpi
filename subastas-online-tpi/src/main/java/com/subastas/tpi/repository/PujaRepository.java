package com.subastas.tpi.repository;

import com.subastas.tpi.entity.Puja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PujaRepository extends JpaRepository<Puja, Long> {

    // trae las pujas de una subasta específica ordenadas por monto
    List<Puja> findBySubastaIdOrderByMontoDesc(Long subastaId);
}
