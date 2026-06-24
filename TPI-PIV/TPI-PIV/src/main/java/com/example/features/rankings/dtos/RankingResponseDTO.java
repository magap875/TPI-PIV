package com.example.features.rankings.dtos;

public record RankingResponseDTO(
        Long id,
        String nombre,
        Integer puntosTotales,
        Integer cantidadResultadosExactos
){}