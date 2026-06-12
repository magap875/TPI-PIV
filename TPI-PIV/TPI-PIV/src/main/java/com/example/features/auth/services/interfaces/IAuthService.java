package com.example.features.auth.services.interfaces;

import com.example.features.auth.dto.request.LoginRequestDTO;
import com.example.features.auth.dto.response.AuthResponseDTO;
import com.example.features.users.dtos.request.UsuarioRegisterDTO;

public interface IAuthService {

    void register(UsuarioRegisterDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
    AuthResponseDTO refresh(String refreshToken);
}