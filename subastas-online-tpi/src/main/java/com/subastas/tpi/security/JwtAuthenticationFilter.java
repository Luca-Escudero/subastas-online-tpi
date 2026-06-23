package com.subastas.tpi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Capturar el Header de Autorización
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        //  Si no hay header o no arranca con "Bearer ", lo dejamos pasar. 
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer el token puro (sacando los primeros 7 caracteres de "Bearer ")
        jwt = authHeader.substring(7);
        userEmail = jwtUtil.extractUsername(jwt); // Acá puede fallar si el token es trucho o expiró

        // Si el token tiene un email y el usuario todavía no está autenticado en la memoria
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Buscamos al usuario en la BD
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Validamos matemáticamente que el token sea de él y no esté vencido
            if (jwtUtil.validateToken(jwt, userDetails)) {
                
                // Creamos la credencial de acceso oficial de Spring
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
