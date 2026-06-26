package com.example.features.users.services.interfaces;

import java.util.List;

import com.example.features.users.dtos.request.UsuarioUpdateDTO;
import com.example.features.users.dtos.response.UsuarioResponseDTO;

public interface IUsuarioService {
    UsuarioResponseDTO getPerfil(String email);
    UsuarioResponseDTO actualizarPerfil(String email, UsuarioUpdateDTO dto);
    void eliminarCuenta(String email);
    List<UsuarioResponseDTO> listarUsuarios();
    UsuarioResponseDTO cambiarRol(Long usuarioId);
}