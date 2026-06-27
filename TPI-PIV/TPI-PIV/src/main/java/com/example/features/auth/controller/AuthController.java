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
import com.example.config.response.BaseResponse;
import com.example.config.exceptions.ErrorResponseDTO;
import com.example.features.auth.dto.request.LoginRequestDTO;
import com.example.features.auth.dto.request.RefreshRequestDTO;
import com.example.features.auth.dto.response.AuthResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "Auth", description = "Registro, login y renovación de tokens")
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor

public class AuthController {
        private final IAuthService authService;

        @Operation(summary = "Registrar un nuevo usuario")
        @ApiResponses({
                @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente"),
                @ApiResponse(responseCode = "400", description = "El email ya está registrado o los datos son inválidos",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PostMapping("/register")
        public ResponseEntity<BaseResponse<Void>> register(@Valid @RequestBody UsuarioRegisterDTO request){
                authService.register(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.ok(null, "Usuario registrado correctamente"));
        }

        @Operation(summary = "Iniciar sesión")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Login exitoso"),
                @ApiResponse(responseCode = "400", description = "Credenciales inválidas",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PostMapping("/login")
        public ResponseEntity<BaseResponse<AuthResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request){
                AuthResponseDTO response = authService.login(request);
                return ResponseEntity.ok(BaseResponse.ok(response, "Login exitoso."));
        }

        @Operation(summary = "Renovar el access token usando un refresh token")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Token renovado correctamente"),
                @ApiResponse(responseCode = "400", description = "Refresh token inválido o expirado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PostMapping("/refresh")
        public ResponseEntity<BaseResponse<AuthResponseDTO>> refresh(@Valid @RequestBody RefreshRequestDTO request){
                AuthResponseDTO response = authService.refresh(request.refreshToken());
                return ResponseEntity.ok(BaseResponse.ok(response, "Token renovado correctamente"));
        }
}