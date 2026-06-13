package com.example.features.miembrosGrupos.dtos.request;

import jakarta.validation.constraints.*;

public record MiembroGrupoRequestDTO(

        @NotNull(message = "El ID del usuario es obligatorio")
        Long usuarioId,

        @NotNull(message = "El ID del grupo es obligatorio")
        Long grupoId
) {
}