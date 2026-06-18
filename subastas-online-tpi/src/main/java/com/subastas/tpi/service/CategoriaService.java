package com.subastas.tpi.service;

import com.subastas.tpi.dto.request.CategoriaRequestDTO;
import com.subastas.tpi.dto.response.CategoriaResponseDTO;
import com.subastas.tpi.entity.Categoria;
import com.subastas.tpi.repository.CategoriaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository){
        this.categoriaRepository = categoriaRepository;
    }

    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO request) {
        Categoria categoriaNueva = toEntityFromResquest(request);
        categoriaRepository.save(categoriaNueva);
        return toResponseFromEntity(categoriaNueva);
    }

    public List<CategoriaResponseDTO> obtenerTodos(){
        List<Categoria> categorias = categoriaRepository.findAll();
        List<CategoriaResponseDTO> response = new ArrayList<>();

        for (Categoria categoria : categorias){
            response.add(toResponseFromEntity(categoria));
        }

        return response;
    }

    public CategoriaResponseDTO obtenerPorId(Long id){
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con el ID: " + id));
        CategoriaResponseDTO response = toResponseFromEntity(categoria);

        return response;
    }

    //Mapeos
    private Categoria toEntityFromResquest(CategoriaRequestDTO dto){
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.nombre());
        return categoria;
    }

    private CategoriaResponseDTO toResponseFromEntity(Categoria categoria){
        CategoriaResponseDTO response = new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNombre()
        );
        return response;
    }
}
