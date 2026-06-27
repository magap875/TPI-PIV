package com.example.features.equipos.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.config.exceptions.ErrorResponseDTO;
import com.example.features.equipos.dtos.request.EquipoCreateDTO;
import com.example.features.equipos.dtos.request.EquipoUpdateDTO;
import com.example.features.equipos.dtos.response.EquipoResponseDTO;
import com.example.features.equipos.services.impl.EquipoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Equipos", description = "Gestión de equipos participantes del torneo")
@RestController
@RequestMapping("/api/equipos")
@RequiredArgsConstructor

public class EquipoController {
        private final EquipoService equipoService;

        @Operation(summary = "Crear un equipo")
        @ApiResponses({
                @ApiResponse(responseCode = "201", description = "Equipo creado correctamente",
                        content = @Content(schema = @Schema(implementation = EquipoResponseDTO.class))),
                @ApiResponse(responseCode = "400", description = "Ya existe un equipo con ese nombre o los datos son inválidos",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<EquipoResponseDTO> crear(@Valid @RequestBody EquipoCreateDTO dto) {
                return ResponseEntity.status(HttpStatus.CREATED).body(equipoService.crear(dto));
        }

        @Operation(summary = "Listar todos los equipos")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Equipos obtenidos correctamente")
        })
        @GetMapping
        public ResponseEntity<List<EquipoResponseDTO>> listar() {
                return ResponseEntity.ok(equipoService.listar());
        }

        @Operation(summary = "Buscar un equipo por ID")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Equipo encontrado correctamente",
                        content = @Content(schema = @Schema(implementation = EquipoResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Equipo no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @GetMapping("/{id}")
        public ResponseEntity<EquipoResponseDTO> buscarPorId(
                @Parameter(description = "ID del equipo", example = "1")
                @PathVariable Long id) {

                return ResponseEntity.ok(equipoService.buscarPorId(id));
        }

        @Operation(summary = "Eliminar un equipo")
        @ApiResponses({
                @ApiResponse(responseCode = "204", description = "Equipo eliminado correctamente"),
                @ApiResponse(responseCode = "400", description = "El equipo posee partidos asociados",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Equipo no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> eliminar(
                @Parameter(description = "ID del equipo", example = "1")
                @PathVariable Long id) {

                equipoService.eliminar(id);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Actualizar un equipo")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Equipo actualizado correctamente",
                        content = @Content(schema = @Schema(implementation = EquipoResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Equipo no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PatchMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<EquipoResponseDTO> actualizarEquipo(
                @Parameter(description = "ID del equipo", example = "1")
                @PathVariable Long id,
                @RequestBody EquipoUpdateDTO dto) {

                return ResponseEntity.ok(equipoService.actualizarEquipo(id, dto));
        }
        }