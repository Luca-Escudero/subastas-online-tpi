package com.subastas.tpi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.subastas.tpi.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desactiva el CSRF. Es obligatorio para que funcionen los POST en APIs REST.
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configuracion las reglas de las rutas
            .authorizeHttpRequests(auth -> auth
                // rutas publicas (Cualquiera puede entrar sin Token)
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/usuarios").permitAll() // Registro
                .requestMatchers("/api/auth/login", "/error").permitAll() // Login y manejo de errores
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() // Swagger
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/categorias/**").permitAll() // Ver el catálogo
                
                // Administracion del sistema (Solo ADMIN)
                // Control total sobre usuarios y la estructura de categorías del sistema
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/usuarios/*/estado").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/categorias/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/categorias/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/categorias/**").hasRole("ADMIN")

                // Gestion vendedores (Solo SELLER)
                // Creación y modificación de sus propios productos y subastas
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/productos/**", "/api/subastas/**").hasRole("SELLER")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/productos/**", "/api/subastas/**").hasRole("SELLER")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/productos/**", "/api/subastas/**").hasRole("SELLER")
                
                // Clientes (Solo USER)
                // Participación en las subastas
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/pujas/**").hasRole("USER")

                // Vista general (Cualquier usuario con un Token válido)
                // Ver productos y subastas activas
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/productos/**", "/api/subastas/**").authenticated()
                
                // Cualquier ruta que no esté explícitamente declarada arriba, exige estar logueado
                .anyRequest().authenticated()
            )

            // Agrego el filtro antes del filtro estándar de Spring
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Este Bean es necesario para que el AuthController pueda inyectar el AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
