package com.example.features.pronosticos.dtos.response;

import java.time.LocalDateTime;
import com.example.features.pronosticos.models.ResultadoTendencia;

public record PronosticoResponseDTO(
        Long id,
        Long usuarioId,
        String nombreUsuario,
        Long partidoId,
        Integer golesLocalPronosticados,
        Integer golesVisitantePronosticados,
        ResultadoTendencia resultadoTendencia,
        Integer puntosObtenidos,
        LocalDateTime fechaCreacion
){}
