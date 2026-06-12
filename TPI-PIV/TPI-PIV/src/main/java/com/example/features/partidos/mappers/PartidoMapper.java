package com.example.features.partidos.mappers;

import com.example.features.partidos.dtos.response.PartidoResponseDTO;
import com.example.features.partidos.models.Partido;

public class PartidoMapper {

    public static PartidoResponseDTO toResponseDTO(Partido partido) {

        return new PartidoResponseDTO(
                partido.getId(),
                partido.getFechaHorarioInicio(),
                partido.getEstado(),
                partido.getEquipoLocal().getId(),
                partido.getEquipoLocal().getNombre(),
                partido.getEquipoVisitante().getId(),
                partido.getEquipoVisitante().getNombre(),
                partido.getGolesLocal(),
                partido.getGolesVisitante(),
                partido.getResultadoTendencia(),
                partido.getFecha().getId(),
                partido.getFecha().getNombre()
        );
    }
}
