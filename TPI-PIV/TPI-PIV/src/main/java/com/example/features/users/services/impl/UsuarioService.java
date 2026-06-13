package com.example.features.users.services.impl;

import org.springframework.stereotype.Service;

import com.example.features.users.services.interfaces.IUsuarioService;
import com.example.config.exceptions.ResourceNotFoundException;
import com.example.features.users.dtos.request.UsuarioUpdateDTO;
import com.example.features.users.dtos.response.UsuarioResponseDTO;
import com.example.features.users.mappers.UsuarioMapper;
import com.example.features.users.models.Usuario;
import com.example.features.users.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponseDTO getPerfil(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return UsuarioMapper.toResponseDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO actualizarPerfil(String email, UsuarioUpdateDTO dto) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (dto.nombre() != null) {
            usuario.setNombre(dto.nombre());
        }

        if (dto.contraseña() != null) {
            usuario.setContraseña(passwordEncoder.encode(dto.contraseña()));
        }

        usuarioRepository.save(usuario);

        return UsuarioMapper.toResponseDTO(usuario);
    }

    @Override
    public void eliminarCuenta(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuarioRepository.delete(usuario);
    }
}