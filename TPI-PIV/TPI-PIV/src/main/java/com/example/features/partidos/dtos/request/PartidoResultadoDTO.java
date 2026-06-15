package com.example.features.partidos.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PartidoResultadoDTO(
        @NotNull
        @PositiveOrZero
        Integer golesLocal,

        @NotNull
        @PositiveOrZero
        Integer golesVisitante
){}