package com.example.features.pronosticos.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.example.features.partidos.models.Partido;
import com.example.features.users.models.Usuario;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pronostico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer golesLocalPronosticados;

    private Integer golesVisitantePronosticados;

    private LocalDateTime fechaCreacion;

    private Integer puntosObtenidos = 0;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;
}
