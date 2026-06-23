package com.subastas.tpi.config;

import com.subastas.tpi.entity.Rol;
import com.subastas.tpi.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Clase para cargar los roles en la BD

@Component
public class DataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;

    public DataSeeder(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Verificamos si existe el rol USER. Si no, lo inyectamos.
        if (rolRepository.findByNombre("USER").isEmpty()) {
            rolRepository.save(new Rol(null, "USER"));
        }
        
        // 2. Verificamos si existe el rol SELLER.
        if (rolRepository.findByNombre("SELLER").isEmpty()) {
            rolRepository.save(new Rol(null, "SELLER"));
        }
        
        // 3. Verificamos si existe el rol ADMIN (para tu uso en el futuro).
        if (rolRepository.findByNombre("ADMIN").isEmpty()) {
            rolRepository.save(new Rol(null, "ADMIN"));
        }
        
        System.out.println(" Roles del sistema verificados/cargados en la base de datos.");
    }
}