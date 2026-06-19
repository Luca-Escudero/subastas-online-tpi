package com.subastas.tpi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")

public class Usuario {

    @Id    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String telefono;

    @Column(columnDefinition = "boolean default true")
    private Boolean activo = true;



}

// Table usuarios {
//   id int [pk, increment]
//   nombre varchar
//   apellido varchar
//   email varchar [unique]
//   password varchar [not null] 
//   telefono varchar
//   bloqueado boolean [default: false]
// }