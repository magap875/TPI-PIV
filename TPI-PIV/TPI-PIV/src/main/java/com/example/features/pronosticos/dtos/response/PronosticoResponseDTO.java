package com.example.features.pronosticos.dtos.response;

import java.time.LocalDateTime;
import com.example.features.partidos.models.ResultadoTendencia;

public record PronosticoResponseDTO(
        Long id,
        Long usuarioId,
        String nombreUsuario,
        Long partidoId,
        Integer golesLocalPronosticados,
        Integer golesVisitantePronosticados,
        Integer puntosObtenidos,
        ResultadoTendencia resultadoTendencia,
        LocalDateTime fechaCreacion
){}
