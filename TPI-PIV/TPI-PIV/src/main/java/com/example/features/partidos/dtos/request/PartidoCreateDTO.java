package com.example.features.partidos.dtos.request;

import java.time.LocalDateTime;

public record PartidoCreateDTO(
        LocalDateTime fechaHorarioInicio,
        Long equipoLocalId,
        Long equipoVisitanteId,
        Long fechaId
){}