package com.subastas.tpi.service;

import com.subastas.tpi.dto.request.CategoriaRequestDTO;
import com.subastas.tpi.dto.response.CategoriaResponseDTO;
import com.subastas.tpi.entity.Categoria;
import com.subastas.tpi.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true) //Se aplica solo a métodos de lectura y nos ahorra una línea de código al ubicarla a nivel de clase
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository){
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
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
