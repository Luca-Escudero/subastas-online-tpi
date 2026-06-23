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
                // Registro y Login (Públicos)
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/usuarios").permitAll()
                .requestMatchers("/api/auth/login", "/error").permitAll()
                
                // Administración de Usuarios (SOLO ADMIN)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/usuarios").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/usuarios/*/estado").hasRole("ADMIN")

                // Endpoints del sistema (Lectura para todos los logueados, escritura solo vendedores)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/productos/**", "/api/categorias/**", "/api/subastas/**").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/productos/**", "/api/subastas/**").hasRole("SELLER")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/productos/**", "/api/subastas/**").hasRole("SELLER")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/productos/**", "/api/subastas/**").hasRole("SELLER")
                
                // Todo lo demás cerrado
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
