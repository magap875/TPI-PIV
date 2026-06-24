package com.example.features.pronosticos.mappers;

import com.example.features.pronosticos.dtos.response.PronosticoResponseDTO;
import com.example.features.pronosticos.models.Pronostico;

public class PronosticoMapper {
    public static PronosticoResponseDTO toResponseDTO(Pronostico p) {
        return new PronosticoResponseDTO(
                p.getId(),
                p.getUsuario().getId(),
                p.getUsuario().getNombre(),
                p.getPartido().getId(),
                p.getGolesLocalPronosticados(),
                p.getGolesVisitantePronosticados(),
                p.getPuntosObtenidos(),
                p.getResultadoTendencia(),
                p.getFechaCreacion()
        );
    }
}