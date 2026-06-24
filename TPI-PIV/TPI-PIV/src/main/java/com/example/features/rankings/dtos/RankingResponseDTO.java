package com.example.features.rankings.dtos;

public record RankingResponseDTO(
        Long usuarioId,
        String nombre,
        Integer puntosTotales,
        Integer cantidadResultadosExactos

){
}