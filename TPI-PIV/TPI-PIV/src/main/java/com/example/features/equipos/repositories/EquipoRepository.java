package com.example.features.equipos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.features.equipos.models.Equipo;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}
