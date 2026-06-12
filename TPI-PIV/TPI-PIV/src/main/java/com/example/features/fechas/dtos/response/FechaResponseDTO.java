package com.example.features.fechas.dtos.response;

import com.example.features.fechas.models.EstadoFecha;

public record FechaResponseDTO(
        Long id,
        String nombre,
        EstadoFecha estado
) {
}