package com.subastas.tpi.service;

import com.subastas.tpi.dto.request.SubastaRequestDTO;
import com.subastas.tpi.dto.response.SubastaResponseDTO;
import com.subastas.tpi.entity.EstadoSubasta;
import com.subastas.tpi.entity.Producto;
import com.subastas.tpi.entity.Subasta;
import com.subastas.tpi.repository.ProductoRepository;
import com.subastas.tpi.repository.SubastaRepository;
import com.subastas.tpi.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SubastaService {

    private final SubastaRepository subastaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialEstadoService historialEstadoService;

    public SubastaService(SubastaRepository subastaRepository, ProductoRepository productoRepository, UsuarioRepository usuarioRepository, HistorialEstadoService historialEstadoService) {
        this.subastaRepository = subastaRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialEstadoService = historialEstadoService;
    }

    @Transactional
    public SubastaResponseDTO crearSubasta(SubastaRequestDTO request){

        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new RuntimeException("Error: El producto con ID " + request.productoId() + " no existe."));

        Subasta nuevaSubasta = toEntityFromRequest(request);
        nuevaSubasta.setProducto(producto);
        nuevaSubasta.setMontoActual(nuevaSubasta.getPrecioInicial());
        nuevaSubasta.setEstado(EstadoSubasta.BORRADOR);

        Subasta subastaGuardada = subastaRepository.save(nuevaSubasta);

        historialEstadoService.registrarCambioEstado(
                subastaGuardada,
                subastaGuardada.getEstado(),
                null,
                "Subasta creada en estado BORRADOR"
        );

        return toResponseFromEntity(subastaGuardada, producto);
    }

    public SubastaResponseDTO publicarSubasta(Long id){
        Subasta subasta = subastaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: La subasta con el ID " + id + " no existe."));

        if (subasta.getEstado() != EstadoSubasta.BORRADOR){
            throw new RuntimeException("Solo se pueden publicar subastas en estado BORRADOR.");
        }

        subasta.setEstado(EstadoSubasta.PUBLICADA);
        subastaRepository.save(subasta);

        historialEstadoService.registrarCambioEstado(
                subasta,
                EstadoSubasta.PUBLICADA,
                null,
                "Subasta publicada"
        );

        return toResponseFromEntity(subasta, subasta.getProducto());
    }

    public List<SubastaResponseDTO> obtenerTodos(){
        List<Subasta> subastas = subastaRepository.findAll();
        List<SubastaResponseDTO> response = new ArrayList<>();

        for (Subasta subasta : subastas){
            response.add(toResponseFromEntity(subasta, subasta.getProducto()));
        }

        return response;
    }

    public SubastaResponseDTO obtenerPorId(Long id){
        Subasta subasta = subastaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subasta no encontrada con el ID: " + id));
        SubastaResponseDTO response = toResponseFromEntity(subasta, subasta.getProducto());

        return response;
    }

    @Transactional
    public void eliminarSubasta(Long id) {
        Subasta subasta = subastaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subasta no encontrada con el ID: " + id));
        subastaRepository.deleteById(id);
    }

    //Mapeos
    private Subasta toEntityFromRequest(SubastaRequestDTO request){
        Subasta subasta = new Subasta();
        subasta.setFechaInicio(request.fechaInicio());
        subasta.setFechaCierre(request.fechaCierre());
        subasta.setPrecioInicial(request.precioInicial());
        subasta.setIncrementoMinimo(request.incrementoMinimo());
        return subasta;
    }

    private SubastaResponseDTO toResponseFromEntity(Subasta subasta, Producto producto){
        SubastaResponseDTO response = new SubastaResponseDTO(
                subasta.getId(),
                producto.getId(),
                producto.getNombre(),
                subasta.getFechaInicio(),
                subasta.getFechaCierre(),
                null,
                subasta.getPrecioInicial(),
                subasta.getEstado(),
                subasta.getMontoActual(),
                subasta.getIncrementoMinimo(),
                null,
                null
        );
        return response;
    }
}
