package com.example.features.partidos.dtos.response;

import java.time.LocalDateTime;
import com.example.features.partidos.models.EstadoPartido;
import com.example.features.pronosticos.models.ResultadoTendencia;

public record PartidoResponseDTO(
        Long id,
        LocalDateTime fechaHorarioInicio,
        EstadoPartido estado,
        Long equipoLocalId,
        String equipoLocal,
        Long equipoVisitanteId,
        String equipoVisitante,
        Integer golesLocal,
        Integer golesVisitante,
        ResultadoTendencia resultadoTendencia,
        Long fechaId,
        String fechaNombre
){}