package com.example.features.users.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.config.response.BaseResponse;
import com.example.features.pronosticos.dtos.response.PronosticoResponseDTO;
import com.example.features.pronosticos.services.interfaces.IPronosticoService;
import com.example.features.users.dtos.request.UsuarioUpdateDTO;
import com.example.features.users.dtos.response.UsuarioResponseDTO;
import com.example.features.users.services.interfaces.IUsuarioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor

public class UsuarioController {
        private final IUsuarioService usuarioService;
        private final IPronosticoService pronosticoService;

        // obtener perfil
        @GetMapping("/me")
        public ResponseEntity<BaseResponse<UsuarioResponseDTO>> getPerfil(Authentication authentication) {
                String email = authentication.getName();

                return ResponseEntity.ok(BaseResponse.ok(usuarioService.getPerfil(email),"Perfil obtenido correctamente"));
        }

        // actualizar perfil
        @PatchMapping("/me")
        public ResponseEntity<BaseResponse<UsuarioResponseDTO>> updatePerfil(Authentication authentication,@RequestBody UsuarioUpdateDTO dto){
                String email = authentication.getName();

                return ResponseEntity.ok(BaseResponse.ok(usuarioService.actualizarPerfil(email, dto),"Perfil actualizado correctamente"));
        }

        // borrar cuenta
        @DeleteMapping("/me")
        public ResponseEntity<BaseResponse<Void>> deleteAccount(Authentication authentication) {
                String email = authentication.getName();
                usuarioService.eliminarCuenta(email);

                return ResponseEntity.ok(BaseResponse.ok(null, "Cuenta eliminada correctamente"));
        }

        // pronosticos del usuario
        @GetMapping("/me/pronosticos")
        public ResponseEntity<List<PronosticoResponseDTO>> misPronosticos(Authentication authentication) {
                return ResponseEntity.ok(pronosticoService.listarPorUsuarioEmail(authentication.getName()));
        }
}