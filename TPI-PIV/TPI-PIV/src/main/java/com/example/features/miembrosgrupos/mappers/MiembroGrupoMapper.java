package com.example.features.miembrosGrupos.mappers;

import com.example.features.miembrosGrupos.dtos.response.MiembroGrupoResponseDTO;
import com.example.features.miembrosGrupos.models.MiembroGrupo;

public class MiembroGrupoMapper {

    public static MiembroGrupoResponseDTO toResponseDTO(MiembroGrupo miembro) {

        return new MiembroGrupoResponseDTO(
                miembro.getId(),
                miembro.getUsuario().getId(),
                miembro.getUsuario().getNombre(),
                miembro.getGrupo().getId(),
                miembro.getGrupo().getNombre(),
                miembro.getFechaIngreso()
        );
    }
}