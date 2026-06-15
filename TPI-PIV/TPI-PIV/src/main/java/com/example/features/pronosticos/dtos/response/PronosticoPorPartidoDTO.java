package com.example.features.pronosticos.dtos.response;

import com.example.features.pronosticos.models.ResultadoTendencia;

public record PronosticoPorPartidoDTO(
        Long usuarioId,
        String nombreUsuario,
        Integer golesLocalPronosticados,
        Integer golesVisitantePronosticados,
        ResultadoTendencia resultadoTendencia,
        Integer puntosObtenidos
){}