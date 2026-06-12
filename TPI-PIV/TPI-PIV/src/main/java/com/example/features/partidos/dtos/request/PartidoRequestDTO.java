package com.example.features.partidos.dtos.request;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import com.example.features.partidos.models.EstadoPartido;
import com.example.features.pronosticos.models.ResultadoTendencia;

public record PartidoRequestDTO(

        @NotNull(message = "La fecha y hora de inicio es obligatoria")
        @Future(message = "La fecha y hora del partido debe ser futura")
        LocalDateTime fechaHorarioInicio,

        @NotNull(message = "El estado del partido es obligatorio")
        EstadoPartido estado,

        @NotNull(message = "El ID del equipo local es obligatorio")
        Long equipoLocalId,

        @NotNull(message = "El ID del equipo visitante es obligatorio")
        Long equipoVisitanteId,

        @PositiveOrZero(message = "Los goles del local no pueden ser negativos")
        Integer golesLocal,

        @PositiveOrZero(message = "Los goles del visitante no pueden ser negativos")
        Integer golesVisitante,

        ResultadoTendencia resultadoTendencia,

        @NotNull(message = "El ID de la fecha es obligatorio")
        Long fechaId
) {
}