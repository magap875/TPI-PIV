package com.example.features.users.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.config.response.BaseResponse;
import com.example.features.users.dtos.request.UsuarioUpdateDTO;
import com.example.features.users.dtos.response.UsuarioResponseDTO;
import com.example.features.users.services.interfaces.IUsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UsuarioResponseDTO>> getPerfil(
            Authentication authentication
    ) {
        String email = authentication.getName();

        return ResponseEntity.ok(
                BaseResponse.ok(
                        usuarioService.getPerfil(email),
                        "Perfil obtenido correctamente"
                )
        );
    }

    @PatchMapping("/me")
    public ResponseEntity<BaseResponse<UsuarioResponseDTO>> updatePerfil(
            Authentication authentication,
            @RequestBody UsuarioUpdateDTO dto
    ) {
        String email = authentication.getName();

        return ResponseEntity.ok(
                BaseResponse.ok(
                        usuarioService.actualizarPerfil(email, dto),
                        "Perfil actualizado correctamente"
                )
        );
    }

    @DeleteMapping("/me")
    public ResponseEntity<BaseResponse<Void>> deleteAccount(
            Authentication authentication
    ) {
        String email = authentication.getName();

        usuarioService.eliminarCuenta(email);

        return ResponseEntity.ok(
                BaseResponse.ok(null, "Cuenta eliminada correctamente")
        );
    }
}