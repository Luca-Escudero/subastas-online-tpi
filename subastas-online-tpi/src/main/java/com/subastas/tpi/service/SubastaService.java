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
import java.time.ZoneOffset;
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
    public SubastaResponseDTO crearSubasta(SubastaRequestDTO request, String email){

        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new RuntimeException("Error: El producto con ID " + request.productoId() + " no existe."));

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Error: El usuario autenticado no existe en el sistema."));

        boolean isAdmin = usuario.getRoles().stream()
                .anyMatch(r -> r.getNombre().equalsIgnoreCase("ROLE_ADMIN"));

        // Si no es ADMIN, verificar que el producto pertenezca al vendedor autenticado
        if (!isAdmin && !producto.getVendedor().getId().equals(usuario.getId())) {
            throw new RuntimeException("No puedes crear una subasta para un producto que no te pertenece.");
        }

        Subasta nuevaSubasta = toEntityFromRequest(request);
        nuevaSubasta.setProducto(producto);
        nuevaSubasta.setMontoActual(nuevaSubasta.getPrecioInicial());
        nuevaSubasta.setEstado(EstadoSubasta.BORRADOR);

        Subasta subastaGuardada = subastaRepository.save(nuevaSubasta);

        historialEstadoService.registrarCambioEstado(
                subastaGuardada,
                subastaGuardada.getEstado(),
                usuario,
                "Subasta creada en estado BORRADOR"
        );

        return toResponseFromEntity(subastaGuardada, producto);
    }

    //Funciones de cambios de estado
    @Transactional
    public void publicarSubasta(Long id, String email){
        Subasta subasta = obtenerSubasta(id);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Error: El usuario autenticado no existe en el sistema."));

        boolean isAdmin = usuario.getRoles().stream()
                .anyMatch(r -> r.getNombre().equalsIgnoreCase("ROLE_ADMIN"));

        // Si no es ADMIN, verificar que el producto pertenezca al vendedor autenticado
        if (!isAdmin && !subasta.getProducto().getVendedor().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permisos para publicar esta subasta (solo el vendedor dueño del producto o un administrador pueden hacerlo).");
        }

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

    @Transactional
    public List<SubastaResponseDTO> obtenerTodos(){
        List<Subasta> subastas = subastaRepository.findAll();
        List<SubastaResponseDTO> response = new ArrayList<>();

        for (Subasta subasta : subastas){
            checkAndCloseIfExpired(subasta);
            response.add(toResponseFromEntity(subasta, subasta.getProducto()));
        }

        return response;
    }

    @Transactional
    public SubastaResponseDTO obtenerPorId(Long id){
        Subasta subasta = obtenerSubasta(id);

        checkAndCloseIfExpired(subasta);

        actualizarEstadoSiCorresponde(subasta);

        return toResponseFromEntity(subasta, subasta.getProducto());
    }

    private void checkAndCloseIfExpired(Subasta subasta) {
        LocalDateTime ahora = LocalDateTime.now(ZoneOffset.UTC);
        
        // 1. Transición: PUBLICADA -> ACTIVA al alcanzar la fecha de inicio (si no expiró)
        if (subasta.getEstado() == EstadoSubasta.PUBLICADA && 
            subasta.getFechaInicio() != null && ahora.compareTo(subasta.getFechaInicio()) >= 0 &&
            (subasta.getFechaCierre() == null || ahora.isBefore(subasta.getFechaCierre()))) {
            
            subasta.setEstado(EstadoSubasta.ACTIVA);
            subastaRepository.save(subasta);
            historialEstadoService.registrarCambioEstado(
                    subasta,
                    EstadoSubasta.ACTIVA,
                    null,
                    "Activación automática por inicio de plazo (Evaluación Perezosa)"
            );
        }
        
        // 2. Transición: PUBLICADA o ACTIVA -> ADJUDICADA o FINALIZADA al alcanzar la fecha de cierre
        if (subasta.getFechaCierre() != null && ahora.isAfter(subasta.getFechaCierre())) {
            if (subasta.getEstado() == EstadoSubasta.ACTIVA || subasta.getEstado() == EstadoSubasta.PUBLICADA) {
                EstadoSubasta estadoAnterior = subasta.getEstado();
                EstadoSubasta nuevoEstado;
                String detalle;
                
                if (subasta.getUsuarioGanador() != null) {
                    nuevoEstado = EstadoSubasta.ADJUDICADA;
                    subasta.setFechaAdjudicacion(ahora);
                    detalle = "Adjudicación automática por vencimiento de fecha de cierre (Evaluación Perezosa). Estado anterior: " + estadoAnterior;
                } else {
                    nuevoEstado = EstadoSubasta.FINALIZADA;
                    detalle = "Cierre automático sin pujas por vencimiento de fecha de cierre (Evaluación Perezosa). Estado anterior: " + estadoAnterior;
                }
                
                subasta.setEstado(nuevoEstado);
                subastaRepository.save(subasta);
                
                historialEstadoService.registrarCambioEstado(
                        subasta,
                        nuevoEstado,
                        null,
                        detalle
                );
            }
        }
    }
    
    @Transactional
    public void eliminarSubasta(Long id, String email) {
        Subasta subasta = subastaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subasta no encontrada con el ID: " + id));

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado en el sistema."));

        boolean isAdmin = usuario.getRoles().stream()
                .anyMatch(r -> r.getNombre().equalsIgnoreCase("ROLE_ADMIN"));

        if (isAdmin) {
            // ADMIN: Puede cancelar cualquier subasta en cualquier estado.
            subasta.setEstado(EstadoSubasta.CANCELADA);
            subastaRepository.save(subasta);

            historialEstadoService.registrarCambioEstado(
                    subasta,
                    EstadoSubasta.CANCELADA,
                    usuario,
                    "Subasta cancelada por el ADMINISTRADOR"
            );
            return;
        }

        // Si no es ADMIN, debe ser el creador/vendedor (SELLER) de la subasta
        if (!subasta.getProducto().getVendedor().getId().equals(usuario.getId())) {
            System.err.println("ADVERTENCIA: Intento de cancelación no autorizado de subasta ID " + id 
                    + ". Dueño del producto (ID: " + subasta.getProducto().getVendedor().getId() 
                    + ", Email: " + subasta.getProducto().getVendedor().getEmail() + "), "
                    + "Usuario autenticado (ID: " + usuario.getId() 
                    + ", Email: " + usuario.getEmail() + ").");
            throw new RuntimeException("No tienes permisos para cancelar esta subasta (solo el vendedor o un administrador pueden hacerlo).");
        }

        // SELLER: Reglas específicas de cancelación
        if (subasta.getEstado() == EstadoSubasta.BORRADOR) {
            // BORRADOR: El SELLER puede cancelar sin restricciones
            subasta.setEstado(EstadoSubasta.CANCELADA);
            subastaRepository.save(subasta);

            historialEstadoService.registrarCambioEstado(
                    subasta,
                    EstadoSubasta.CANCELADA,
                    usuario,
                    "Subasta en borrador cancelada por el vendedor"
            );
        } else if (subasta.getEstado() == EstadoSubasta.PUBLICADA || subasta.getEstado() == EstadoSubasta.ACTIVA) {
            // PUBLICADA / ACTIVA: El SELLER solo puede cancelar si la subasta no tiene pujas (sin usuario ganador)
            if (subasta.getUsuarioGanador() != null) {
                throw new RuntimeException("No se puede cancelar la subasta porque ya recibió al menos una puja. Solo un administrador puede intervenir.");
            }
            subasta.setEstado(EstadoSubasta.CANCELADA);
            subastaRepository.save(subasta);

            historialEstadoService.registrarCambioEstado(
                    subasta,
                    EstadoSubasta.CANCELADA,
                    usuario,
                    "Subasta sin pujas cancelada por el vendedor"
            );
        } else {
            // Cualquier otro estado (FINALIZADA, ADJUDICADA, etc.) no está permitido para el SELLER
            throw new RuntimeException("No tienes permisos para cancelar la subasta en su estado actual (" + subasta.getEstado() + "). Solo un administrador puede intervenir.");
        }
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
                subasta.getFechaAdjudicacion(),
                subasta.getPrecioInicial(),
                subasta.getEstado(),
                subasta.getMontoActual(),
                subasta.getIncrementoMinimo(),
                subasta.getUsuarioGanador() != null ? subasta.getUsuarioGanador().getId() : null,
                subasta.getUsuarioGanador() != null ? subasta.getUsuarioGanador().getNombre() : null,
                subasta.getUsuarioGanador() != null ? subasta.getUsuarioGanador().getEmail() : null
        );
        return response;
    }
}
