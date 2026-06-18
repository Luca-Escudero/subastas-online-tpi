package com.subastas.tpi.service;

import com.subastas.tpi.dto.response.CategoriaResponseDTO;
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

        // Validar que el usuario (vendedor) exista
        Usuario vendedor = usuarioRepository.findById(request.vendedorId())
                .orElseThrow(() -> new RuntimeException("Error: El usuario con ID " + request.vendedorId() + " no existe."));

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

    // Mapeos
    private Producto toEntityFromRequest(ProductoRequestDTO request){
        Producto producto = new Producto();
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setImagenUrl(request.imagenUrl());
        return producto;
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
