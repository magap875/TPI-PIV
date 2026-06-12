package com.example.features.users.dtos.response;

import com.example.features.users.models.Rol;

public record UsuarioResponseDTO(

        Long id,
        String nombre,
        String email,
        Rol rol,
        Integer puntosTotales,
        Integer cantidadResultadosExactos

) {
}
