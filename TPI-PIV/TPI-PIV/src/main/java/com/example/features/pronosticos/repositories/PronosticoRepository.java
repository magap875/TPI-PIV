package com.example.features.pronosticos.repositories;

import com.example.features.pronosticos.models.Pronostico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PronosticoRepository extends JpaRepository<Pronostico, Long> {
    Optional<Pronostico> findByUsuarioIdAndPartidoId(Long usuarioId, Long partidoId);
    List<Pronostico> findByUsuarioId(Long usuarioId);
    List<Pronostico> findByPartidoId(Long partidoId);
}