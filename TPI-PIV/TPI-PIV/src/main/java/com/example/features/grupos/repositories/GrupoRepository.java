package com.example.features.grupos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.features.grupos.models.Grupo;
import java.util.Optional;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    boolean existsByCodigoInvitacion(String codigoInvitacion);

    Optional<Grupo> findByCodigoInvitacion(String codigoInvitacion);
}