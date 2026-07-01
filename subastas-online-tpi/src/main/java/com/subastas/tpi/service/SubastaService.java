package com.subastas.tpi.service;

import com.subastas.tpi.dto.request.SubastaRequestDTO;
import com.subastas.tpi.dto.response.SubastaResponseDTO;
import com.subastas.tpi.entity.EstadoSubasta;
import com.subastas.tpi.entity.Producto;
import com.subastas.tpi.entity.Subasta;
import com.subastas.tpi.entity.Usuario;
import com.subastas.tpi.repository.ProductoRepository;
import com.subastas.tpi.repository.SubastaRepository;
import com.subastas.tpi.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    //Funciones de cambios de estado
    @Transactional
    public void publicarSubasta(Long id){
        Subasta subasta = obtenerSubasta(id);

        if (subasta.getEstado() != EstadoSubasta.BORRADOR){
            throw new RuntimeException("Solo se pueden publicar subastas en estado BORRADOR.");
        }

        cambiarEstado(subasta,
                EstadoSubasta.PUBLICADA,
                null,
                "Subasta publicada.");
    }

    @Transactional
    public void activarSubasta(Long id) {

        Subasta subasta = obtenerSubasta(id);

        if (subasta.getEstado() != EstadoSubasta.PUBLICADA) {
            throw new RuntimeException("Solo pueden activarse subastas en estado PUBLICADA.");
        }

        if (LocalDateTime.now().isBefore(subasta.getFechaInicio())) {
            throw new RuntimeException("La fecha de inicio de la subasta aún no fue alcanzada.");
        }

        cambiarEstado(subasta,
                EstadoSubasta.ACTIVA,
                null,
                "Subasta activada automáticamente."
        );
    }

    @Transactional
    public void finalizarSubasta(Long id) {

        Subasta subasta = obtenerSubasta(id);

        if (subasta.getEstado() != EstadoSubasta.ACTIVA) {
            throw new RuntimeException("Solo pueden finalizarse subastas ACTIVAS.");
        }

        if (LocalDateTime.now().isBefore(subasta.getFechaCierre())) {
            throw new RuntimeException("La fecha de cierre aún no fue alcanzada.");
        }

        cambiarEstado(
                subasta,
                EstadoSubasta.FINALIZADA,
                null,
                "Subasta finalizada sin ofertas."
        );
    }

    @Transactional
    public void adjudicarSubasta(Long id) {

        Subasta subasta = obtenerSubasta(id);

        if (subasta.getEstado() != EstadoSubasta.ACTIVA) {
            throw new RuntimeException("Solo pueden adjudicarse subastas ACTIVAS.");
        }

        if (LocalDateTime.now().isBefore(subasta.getFechaCierre())) {
            throw new RuntimeException("La fecha de cierre aún no fue alcanzada.");
        }

        cambiarEstado(
                subasta,
                EstadoSubasta.ADJUDICADA,
                null,
                "Subasta adjudicada."
        );
    }

    @Transactional
    public void cancelarSubasta(Long id, String motivo) {

        Subasta subasta = obtenerSubasta(id);

        if (subasta.getEstado() == EstadoSubasta.FINALIZADA
                || subasta.getEstado() == EstadoSubasta.ADJUDICADA
                || subasta.getEstado() == EstadoSubasta.CANCELADA) {

            throw new RuntimeException("La subasta ya no puede cancelarse.");
        }

        cambiarEstado(
                subasta,
                EstadoSubasta.CANCELADA,
                null,
                motivo
        );
    }

    @Transactional
    public void abrirDisputa(Long id, String motivo) {

        Subasta subasta = obtenerSubasta(id);

        if (subasta.getEstado() != EstadoSubasta.ADJUDICADA) {
            throw new RuntimeException("Solo una subasta adjudicada puede entrar en disputa.");
        }

        cambiarEstado(
                subasta,
                EstadoSubasta.EN_DISPUTA,
                null,
                motivo
        );
    }

    @Transactional
    public void resolverDisputa(Long id, EstadoSubasta nuevoEstado, String detalle) {

        Subasta subasta = obtenerSubasta(id);

        if (subasta.getEstado() != EstadoSubasta.EN_DISPUTA) {
            throw new RuntimeException("La subasta no está en disputa.");
        }

        if (nuevoEstado != EstadoSubasta.ADJUDICADA
                && nuevoEstado != EstadoSubasta.FINALIZADA
                && nuevoEstado != EstadoSubasta.CANCELADA) {

            throw new RuntimeException("Estado de resolución inválido.");
        }

        cambiarEstado(
                subasta,
                nuevoEstado,
                null,
                detalle
        );
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
        Subasta subasta = obtenerSubasta(id);

        actualizarEstadoSiCorresponde(subasta);

        return toResponseFromEntity(subasta, subasta.getProducto());
    }

    @Transactional
    public void eliminarSubasta(Long id) {
        Subasta subasta = subastaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subasta no encontrada con el ID: " + id));
        subastaRepository.deleteById(id);
    }

    @Transactional
    private void actualizarEstadoSiCorresponde(Subasta subasta) {

        LocalDateTime ahora = LocalDateTime.now();

        if (subasta.getEstado() == EstadoSubasta.PUBLICADA &&
                !ahora.isBefore(subasta.getFechaInicio())) {

            cambiarEstado(
                    subasta,
                    EstadoSubasta.ACTIVA,
                    null,
                    "Subasta activada automáticamente."
            );
        }

        if (subasta.getEstado() == EstadoSubasta.ACTIVA &&
                !ahora.isBefore(subasta.getFechaCierre())) {

            finalizarSubasta(subasta.getId());
        }
    }

    @Transactional
    public void actualizarEstadosAutomaticamente() {

        List<Subasta> subastas = subastaRepository.findAll();

        for (Subasta subasta : subastas) {
            actualizarEstadoSiCorresponde(subasta);
        }
    }

    //Funciones para evitar duplicar código
    private void cambiarEstado(Subasta subasta,
                               EstadoSubasta nuevoEstado,
                               Usuario usuario,
                               String detalle) {

        subasta.setEstado(nuevoEstado);

        subastaRepository.save(subasta);

        historialEstadoService.registrarCambioEstado(
                subasta,
                nuevoEstado,
                usuario,
                detalle
        );
    }

    private Subasta obtenerSubasta(Long id) {
        return subastaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subasta no encontrada."));
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
