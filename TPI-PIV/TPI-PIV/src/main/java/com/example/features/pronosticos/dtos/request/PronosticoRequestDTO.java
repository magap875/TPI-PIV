package com.example.features.pronosticos.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PronosticoRequestDTO(
        @NotNull(message = "Los goles del local son obligatorios")
        @PositiveOrZero(message = "No puede ser negativo")
        Integer golesLocalPronosticados,

        @NotNull(message = "Los goles del visitante son obligatorios")
        @PositiveOrZero(message = "No puede ser negativo")
        Integer golesVisitantePronosticados
){}