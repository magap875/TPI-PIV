package com.example.features.users.mappers;

import com.example.features.users.dtos.request.UsuarioRegisterDTO;
import com.example.features.users.dtos.response.UsuarioResponseDTO;
import com.example.features.users.models.Rol;
import com.example.features.users.models.Usuario;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioRegisterDTO dto) {

        Usuario usuario = new Usuario();

        usuario.setNombre(dto.nombre());
        usuario.setEmail(dto.email());
        usuario.setContraseña(dto.contraseña());
        usuario.setRol(Rol.USER); // rol fijo por seguridad

        return usuario;
    }

    public static UsuarioResponseDTO toResponseDTO(Usuario usuario) {

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getPuntosTotales(),
                usuario.getCantidadResultadosExactos()
        );
    }
}