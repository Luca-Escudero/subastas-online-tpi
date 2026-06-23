package com.subastas.tpi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.subastas.tpi.dto.request.LoginRequestDTO;
import com.subastas.tpi.dto.response.JwtResponseDTO;
import com.subastas.tpi.security.CustomUserDetailsService;
import com.subastas.tpi.security.JwtUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;


    public AuthController(AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService,
            JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody LoginRequestDTO request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        final UserDetails userdetails = userDetailsService.loadUserByUsername(request.email());

        final String jwt = jwtUtil.generateToken(userdetails);

        return ResponseEntity.ok(new JwtResponseDTO(jwt));
    }
    

}