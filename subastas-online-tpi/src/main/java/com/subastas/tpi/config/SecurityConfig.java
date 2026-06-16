package com.subastas.tpi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desactivamos CSRF. Es obligatorio para que funcionen los POST en APIs REST.
            .csrf(AbstractHttpConfigurer::disable)
            
            // 2. Configuramos las reglas de las rutas
            .authorizeHttpRequests(auth -> auth
                // Dejamos 100% público el endpoint de usuarios (para que cualquiera pueda registrarse)
                .requestMatchers("/api/usuarios", "/api/usuarios/**", "/error").permitAll() 
                // A todo el resto del sistema, le pedimos autenticación
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
