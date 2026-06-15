package com.example.features.equipos.mappers;

import com.example.features.equipos.dtos.request.EquipoCreateDTO;
import com.example.features.equipos.dtos.response.EquipoResponseDTO;
import com.example.features.equipos.models.Equipo;

public class EquipoMapper {

    public static Equipo toEntity(EquipoCreateDTO dto) {
        Equipo equipo = new Equipo();
        equipo.setNombre(dto.nombre());
        return equipo;
    }

    public static EquipoResponseDTO toResponseDTO(Equipo equipo) {
        return new EquipoResponseDTO(
                equipo.getId(),
                equipo.getNombre()
        );
    }
}
