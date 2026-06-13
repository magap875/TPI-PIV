package com.example.features.miembrosgrupos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.features.miembrosgrupos.models.MiembroGrupo;
import java.util.List;
import java.util.Optional;

public interface MiembroGrupoRepository extends JpaRepository<MiembroGrupo, Long> {

    boolean existsByUsuarioIdAndGrupoId(Long usuarioId, Long grupoId);

    Optional<MiembroGrupo> findByUsuarioIdAndGrupoId(Long usuarioId, Long grupoId);

    List<MiembroGrupo> findByGrupoId(Long grupoId);

    List<MiembroGrupo> findByUsuarioId(Long usuarioId);
}