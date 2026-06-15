package com.example.features.partidos.dtos.request;

import java.time.LocalDateTime;

public record PartidoUpdateDTO(
        LocalDateTime fechaHorarioInicio,
        Long equipoLocalId,
        Long equipoVisitanteId
){}