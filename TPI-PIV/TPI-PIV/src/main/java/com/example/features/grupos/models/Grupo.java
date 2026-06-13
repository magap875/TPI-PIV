package com.example.features.grupos.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

import com.example.features.miembrosGrupos.models.MiembroGrupo;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(unique = true)
    private String codigoInvitacion;

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MiembroGrupo> miembros = new ArrayList<>();
}