package com.example.features.grupos.mappers;

import com.example.features.grupos.dtos.request.GrupoRequestDTO;
import com.example.features.grupos.dtos.response.GrupoResponseDTO;
import com.example.features.grupos.models.Grupo;

public class GrupoMapper {

    public static Grupo toEntity(GrupoRequestDTO dto) {

        Grupo grupo = new Grupo();

        grupo.setNombre(dto.nombre());
        grupo.setCodigoInvitacion(dto.codigoInvitacion());

        return grupo;
    }

    public static GrupoResponseDTO toResponseDTO(Grupo grupo) {

        return new GrupoResponseDTO(
                grupo.getId(),
                grupo.getNombre(),
                grupo.getCodigoInvitacion()
        );
    }
}
