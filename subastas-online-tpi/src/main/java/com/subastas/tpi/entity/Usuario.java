package com.subastas.tpi.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
    private Integer id;

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
        @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles", // El nombre de la tabla puente del der
        joinColumns = @JoinColumn(name = "usuario_id"), // La columna que apunta a ESTA clase (Usuario)
        inverseJoinColumns = @JoinColumn(name = "rol_id") // La columna que apunta a la OTRA clase (Rol)
    )
    private Set<Rol> roles = new HashSet<>();


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