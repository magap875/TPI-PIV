package com.example.features.pronosticos.mappers;

import com.example.features.pronosticos.dtos.response.PronosticoResponseDTO;
import com.example.features.pronosticos.models.Pronostico;

public class PronosticoMapper {

    public static PronosticoResponseDTO toResponseDTO(Pronostico pronostico) {

        return new PronosticoResponseDTO(
                pronostico.getId(),
                pronostico.getUsuario().getId(),
                pronostico.getUsuario().getNombre(),
                pronostico.getPartido().getId(),
                pronostico.getGolesLocalPronosticados(),
                pronostico.getGolesVisitantePronosticados(),
                pronostico.getFechaCreacion(),
                pronostico.getPuntosObtenidos()
        );
    }
}