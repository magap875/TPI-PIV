package com.example.features.users.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String contraseña;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Column(nullable = false)
    private Integer puntosTotales = 0;

    @Column(nullable = false)
    private Integer cantidadResultadosExactos = 0;
}