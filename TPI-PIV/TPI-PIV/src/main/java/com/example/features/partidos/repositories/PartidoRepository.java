package com.example.features.partidos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.features.partidos.models.EstadoPartido;
import com.example.features.partidos.models.Partido;
import java.util.List;

public interface PartidoRepository extends JpaRepository<Partido, Long> {
    List<Partido> findByFechaId(Long fechaId);
    boolean existsByEquipoLocalIdOrEquipoVisitanteId(Long equipoLocalId, Long equipoVisitanteId);
    List<Partido> findByEstado(EstadoPartido estado);
}