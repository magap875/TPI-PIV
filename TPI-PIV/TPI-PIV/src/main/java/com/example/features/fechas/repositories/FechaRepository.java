package com.example.features.fechas.repositories;

import com.example.features.fechas.models.Fecha;
import com.example.features.fechas.models.EstadoFecha;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FechaRepository extends JpaRepository<Fecha, Long> {
    boolean existsByNombre(String nombre);
    Optional<Fecha> findByNombre(String nombre);
    List<Fecha> findByEstado(EstadoFecha estado);
}