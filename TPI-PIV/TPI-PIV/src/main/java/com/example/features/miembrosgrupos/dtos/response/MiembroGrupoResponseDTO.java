package com.example.features.miembrosGrupos.dtos.response;

import java.time.LocalDateTime;

public record MiembroGrupoResponseDTO(

        Long id,

        Long usuarioId,
        String usuarioNombre,

        Long grupoId,
        String grupoNombre,

        LocalDateTime fechaIngreso

) {
}