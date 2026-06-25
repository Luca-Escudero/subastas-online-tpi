package com.subastas.tpi.repository;

import com.subastas.tpi.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    // trae las notificaciones de un usuario y las ordena
    List<Notificacion> findByDestinatarioIdOrderByFechaEnvioDesc(Long usuarioId);
}
