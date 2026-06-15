package com.example.features.fechas.mappers;

import com.example.features.fechas.dtos.request.FechaCreateDTO;
import com.example.features.fechas.dtos.response.FechaResponseDTO;
import com.example.features.fechas.models.EstadoFecha;
import com.example.features.fechas.models.Fecha;

public class FechaMapper {

    public static Fecha toEntity(FechaCreateDTO dto) {
        Fecha fecha = new Fecha();
        fecha.setNombre(dto.nombre());
        fecha.setEstado(EstadoFecha.PROGRAMADA);

        return fecha;
    }

    public static FechaResponseDTO toResponseDTO(Fecha fecha) {
        return new FechaResponseDTO(
                fecha.getId(),
                fecha.getNombre(),
                fecha.getEstado()
        );
    }
}