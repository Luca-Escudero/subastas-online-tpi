package com.subastas.tpi.service;

import com.subastas.tpi.dto.response.HistorialEstadoResponseDTO;
import com.subastas.tpi.entity.EstadoSubasta;
import com.subastas.tpi.entity.HistorialEstado;
import com.subastas.tpi.entity.Subasta;
import com.subastas.tpi.entity.Usuario;
import com.subastas.tpi.repository.HistorialEstadoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HistorialEstadoService {

    private final HistorialEstadoRepository historialEstadoRepository;

    public HistorialEstadoService (HistorialEstadoRepository historialEstadoRepository){
        this.historialEstadoRepository = historialEstadoRepository;
    }

    public void registrarCambioEstado(Subasta subasta, EstadoSubasta nuevoEstado, Usuario usuario, String detalle) {
        HistorialEstado historial = new HistorialEstado();

        historial.setSubasta(subasta);
        historial.setNombreEstado(nuevoEstado);
        historial.setFecha(LocalDateTime.now());
        historial.setUsuario(usuario);
        historial.setDetalle(detalle);

        historialEstadoRepository.save(historial);
    }

    public List<HistorialEstadoResponseDTO> obtenerHistorialPorSubasta(Long subastaId) {
        return toResponseFromEntity(historialEstadoRepository.findBySubastaIdOrderByFechaAsc(subastaId));
    }

    private List<HistorialEstadoResponseDTO> toResponseFromEntity(List<HistorialEstado> historialEstados){
        List<HistorialEstadoResponseDTO> responses = new ArrayList<>();

        for (HistorialEstado historial : historialEstados) {

            Long usuarioId = null;
            if (historial.getUsuario() != null) {
                usuarioId = historial.getUsuario().getId();
            }

            HistorialEstadoResponseDTO dto = new HistorialEstadoResponseDTO(
                    historial.getNombreEstado(),
                    historial.getFecha(),
                    usuarioId,
                    historial.getDetalle()
            );

            responses.add(dto);
        }

        return responses;
    }
}
