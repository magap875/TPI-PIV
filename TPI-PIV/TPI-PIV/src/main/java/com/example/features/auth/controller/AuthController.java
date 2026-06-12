package com.example.features.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.features.auth.services.interfaces.*;
import com.example.features.users.dtos.request.UsuarioRegisterDTO;
import com.example.config.BaseResponse;
import com.example.features.auth.dto.request.LoginRequestDTO;
import com.example.features.auth.dto.request.RefreshRequestDTO;
import com.example.features.auth.dto.response.AuthResponseDTO;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Void>> register(
            @Valid @RequestBody UsuarioRegisterDTO request
    ) {
        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ok(null, "Usuario registrado correctamente"));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request
    ) {
        AuthResponseDTO response = authService.login(request);

        return ResponseEntity.ok(
                BaseResponse.ok(response, "Login exitoso")
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<AuthResponseDTO>> refresh(
            @Valid @RequestBody RefreshRequestDTO request
    ) {
        AuthResponseDTO response = authService.refresh(request.refreshToken());

        return ResponseEntity.ok(
                BaseResponse.ok(response, "Token renovado correctamente")
        );
    }
}