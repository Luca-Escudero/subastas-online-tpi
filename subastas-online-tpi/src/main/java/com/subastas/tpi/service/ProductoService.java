package com.subastas.tpi.service;

import com.subastas.tpi.dto.response.CategoriaResponseDTO;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.subastas.tpi.dto.request.ProductoRequestDTO;
import com.subastas.tpi.dto.response.ProductoResponseDTO;
import com.subastas.tpi.entity.Categoria;
import com.subastas.tpi.entity.Producto;
import com.subastas.tpi.entity.Usuario;
import com.subastas.tpi.repository.CategoriaRepository;
import com.subastas.tpi.repository.ProductoRepository;
import com.subastas.tpi.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository,
            UsuarioRepository usuarioRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO request) {
        // Validar que la categoría exista
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RuntimeException("Error: La categoría con ID " + request.categoriaId() + " no existe."));

        // Extraer el id del vendedor del token
        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();

        
        // Validar que el usuario (vendedor) exista
        Usuario vendedor = usuarioRepository.findByEmail(emailAutenticado)
                .orElseThrow(() -> new RuntimeException("Error: El usuario autenticado no existe en el sistema."));

        Producto nuevoProducto = toEntityFromRequest(request);
        nuevoProducto.setCategoria(categoria);
        nuevoProducto.setVendedor(vendedor);

        Producto productoGuardado = productoRepository.save(nuevoProducto);

        return toResponseFromEntity(productoGuardado, categoria, vendedor);
    }

    public List<ProductoResponseDTO> obtenerTodos(){
        List<Producto> productos = productoRepository.findAll();
        List<ProductoResponseDTO> response = new ArrayList<>();

        for (Producto producto : productos){
            response.add(toResponseFromEntity(producto, producto.getCategoria(), producto.getVendedor()));
        }

        return response;
    }

    public ProductoResponseDTO obtenerPorId(Long id){
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con el ID: " + id));
        ProductoResponseDTO response = toResponseFromEntity(producto, producto.getCategoria(), producto.getVendedor());

        return response;
    }

    @Transactional
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO request){
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con el ID: " + id));

        updateEntityFromRequest(producto, request);

        if (request.categoriaId() != null) {
            Categoria nuevaCategoria = categoriaRepository.findById(request.categoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            producto.setCategoria(nuevaCategoria);
        }

        Producto productoActualizado = productoRepository.save(producto);

        return toResponseFromEntity(productoActualizado, productoActualizado.getCategoria(), productoActualizado.getVendedor());
    }

    @Transactional
    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con el ID: " + id));
        productoRepository.deleteById(id);
    }

    // Mapeos
    private Producto toEntityFromRequest(ProductoRequestDTO request){
        Producto producto = new Producto();
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setImagenUrl(request.imagenUrl());
        return producto;
    }

    private void updateEntityFromRequest(Producto producto, ProductoRequestDTO request){
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setImagenUrl(request.imagenUrl());
    }

    private ProductoResponseDTO toResponseFromEntity(Producto producto, Categoria categoria, Usuario usuario){
        ProductoResponseDTO response = new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getImagenUrl(),
                categoria.getId(),
                categoria.getNombre(),
                usuario.getId(),
                usuario.getNombre()
        );
        return response;
    }
}
