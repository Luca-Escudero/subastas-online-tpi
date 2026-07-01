package com.subastas.tpi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Habilita CORS para que el front (servido como archivos estáticos en otro
 * puerto, ej. Live Server en :5500) pueda llamar a esta API en :8080.
 * Sin esto, el navegador bloquea las peticiones del front aunque Postman
 * o Swagger funcionen perfecto.
 *
 * Se expone como CorsConfigurationSource (no como CorsFilter) para que se
 * integre con la cadena de filtros de Spring Security vía http.cors(...)
 * en SecurityConfig, y así corra ANTES del filtro JWT.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of(
                "http://localhost:5500",
                "http://127.0.0.1:5500",
                "http://localhost:3000"
        ));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
