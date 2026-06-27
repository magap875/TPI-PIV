package com.example.features.pronosticos.controllers;

import com.example.config.exceptions.ErrorResponseDTO;
import com.example.features.pronosticos.dtos.request.PronosticoRequestDTO;
import com.example.features.pronosticos.dtos.response.PronosticoResponseDTO;
import com.example.features.pronosticos.services.interfaces.IPronosticoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Pronosticos", description = "Gestión de pronósticos de los usuarios sobre los partidos")
@RestController
@AllArgsConstructor
@RequestMapping("/api/pronosticos")
public class PronosticoController {
        private final IPronosticoService pronosticoService;

        @Operation(summary = "Crear o actualizar el pronóstico del usuario autenticado para un partido")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Pronóstico registrado o actualizado correctamente"),
                @ApiResponse(responseCode = "400", description = "El partido no admite pronósticos en su estado actual, o ya venció el tiempo límite",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Partido o usuario no encontrados",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PostMapping("/{partidoId}")
        public ResponseEntity<PronosticoResponseDTO> crearOActualizar(
                @Parameter(description = "ID del partido", example = "1")
                @PathVariable Long partidoId, @Valid @RequestBody PronosticoRequestDTO dto, Authentication authentication){
                return ResponseEntity.ok(pronosticoService.crearOActualizar(authentication.getName(),partidoId,dto)
                );
        }

        @Operation(summary = "Obtener el pronóstico del usuario autenticado para un partido específico")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Devuelve el pronóstico si existe, o null si el usuario todavía no pronosticó este partido")
        })
        @GetMapping("/mi-pronostico/{partidoId}")
        public ResponseEntity<PronosticoResponseDTO> obtenerMiPronostico(
                @Parameter(description = "ID del partido", example = "1")
                @PathVariable Long partidoId, Authentication authentication){
                return ResponseEntity.ok(pronosticoService.obtenerPorUsuarioYPartido(authentication.getName(), partidoId));
        }

        @Operation(summary = "Listar los pronósticos de un usuario específico")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Pronósticos obtenidos correctamente (lista vacía si el usuario no existe o no tiene pronósticos)")
        })
        @GetMapping("/usuario/{usuarioId}")
        public ResponseEntity<List<PronosticoResponseDTO>> porUsuario(
                @Parameter(description = "ID del usuario", example = "1")
                @PathVariable Long usuarioId){
                return ResponseEntity.ok(pronosticoService.listarPorUsuario(usuarioId));
        }

        @Operation(summary = "Listar los pronósticos del usuario autenticado")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Pronósticos obtenidos correctamente"),
                @ApiResponse(responseCode = "404", description = "Usuario autenticado no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @GetMapping("/mis-pronosticos")
        public ResponseEntity<List<PronosticoResponseDTO>> misPronosticos(Authentication authentication){
                return ResponseEntity.ok(pronosticoService.listarPorUsuarioEmail(authentication.getName()));
        }

        @Operation(summary = "Listar los pronósticos de un partido (solo ADMIN)")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Pronósticos obtenidos correctamente"),
                @ApiResponse(responseCode = "400", description = "Todavía no se pueden ver los pronósticos de este partido",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Partido no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @GetMapping("/partido/{partidoId}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<PronosticoResponseDTO>> porPartido(
                @Parameter(description = "ID del partido", example = "1")
                @PathVariable Long partidoId) {
                return ResponseEntity.ok(pronosticoService.listarPorPartido(partidoId)
                );
        }
}