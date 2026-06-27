package com.example.features.users.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.config.response.BaseResponse;
import com.example.config.exceptions.ErrorResponseDTO;
import com.example.features.users.dtos.request.UsuarioUpdateDTO;
import com.example.features.users.dtos.response.UsuarioResponseDTO;
import com.example.features.users.services.interfaces.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Usuarios", description = "Gestión del perfil del usuario autenticado")
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor

public class UsuarioController {
        private final IUsuarioService usuarioService;

        @Operation(summary = "Obtener el perfil del usuario autenticado")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Perfil obtenido correctamente"),
                @ApiResponse(responseCode = "404", description = "Usuario autenticado no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @GetMapping("/me")
        public ResponseEntity<BaseResponse<UsuarioResponseDTO>> getPerfil(Authentication authentication) {
                String email = authentication.getName();

                return ResponseEntity.ok(BaseResponse.ok(usuarioService.getPerfil(email),"Perfil obtenido correctamente"));
        }

        @Operation(summary = "Actualizar el perfil del usuario autenticado")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Perfil actualizado correctamente"),
                @ApiResponse(responseCode = "404", description = "Usuario autenticado no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PatchMapping("/me")
        public ResponseEntity<BaseResponse<UsuarioResponseDTO>> updatePerfil(Authentication authentication,@RequestBody UsuarioUpdateDTO dto){
                String email = authentication.getName();

                return ResponseEntity.ok(BaseResponse.ok(usuarioService.actualizarPerfil(email, dto),"Perfil actualizado correctamente"));
        }

        @Operation(summary = "Eliminar la cuenta del usuario autenticado")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Cuenta eliminada correctamente"),
                @ApiResponse(responseCode = "404", description = "Usuario autenticado no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @DeleteMapping("/me")
        public ResponseEntity<BaseResponse<Void>> deleteAccount(Authentication authentication) {
                String email = authentication.getName();
                usuarioService.eliminarCuenta(email);

                return ResponseEntity.ok(BaseResponse.ok(null, "Cuenta eliminada correctamente"));
        }
}