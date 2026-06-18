package com.subastas.tpi.service;

import org.springframework.stereotype.Service;

import com.subastas.tpi.dto.request.ProductoRequestDTO;
import com.subastas.tpi.dto.response.ProductoResponseDTO;
import com.subastas.tpi.entity.Categoria;
import com.subastas.tpi.entity.Producto;
import com.subastas.tpi.entity.Usuario;
import com.subastas.tpi.repository.CategoriaRepository;
import com.subastas.tpi.repository.ProductoRepository;
import com.subastas.tpi.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
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

        
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(request.nombre());
        nuevoProducto.setDescripcion(request.descripcion());
        nuevoProducto.setImagenUrl(request.imagenUrl());
        nuevoProducto.setCategoria(categoria);
        nuevoProducto.setVendedor(vendedor);

        
        Producto productoGuardado = productoRepository.save(nuevoProducto);

       
        return new ProductoResponseDTO(
                productoGuardado.getId(),
                productoGuardado.getNombre(),
                productoGuardado.getDescripcion(),
                productoGuardado.getImagenUrl(),
                categoria.getId(),
                categoria.getNombre(),
                vendedor.getId(),
                vendedor.getNombre()
        );
    }
}
