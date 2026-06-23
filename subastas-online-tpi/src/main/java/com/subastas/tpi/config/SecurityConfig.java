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
            // 1. Desactivamos CSRF. Es obligatorio para que funcionen los POST en APIs REST.
            .csrf(AbstractHttpConfigurer::disable)
            
            // 2. Configuramos las reglas de las rutas
            .authorizeHttpRequests(auth -> auth
                // Dejamos 100% público el endpoint de usuarios (para que cualquiera pueda registrarse)
                .requestMatchers("/api/usuarios", "/api/auth/login", "/error").permitAll() 
                // Por ahora, dejamos libre productos (después lo borran)
                .requestMatchers("/api/productos", "/api/productos/**").permitAll()
                // A todo el resto del sistema, le pedimos autenticación
                .anyRequest().authenticated()
            )
            // Agregamos TU filtro ANTES del filtro estándar de Spring
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
