package com.example.features.fechas.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

import com.example.features.partidos.models.Partido;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fecha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private EstadoFecha estado;

    @OneToMany(mappedBy = "fecha", cascade = CascadeType.ALL)
    private List<Partido> partidos = new ArrayList<>();
}
