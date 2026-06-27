package com.example.features.fechas.controllers;

import com.example.config.exceptions.ErrorResponseDTO;
import com.example.features.fechas.dtos.request.FechaCreateDTO;
import com.example.features.fechas.dtos.request.FechaUpdateDTO;
import com.example.features.fechas.dtos.response.FechaResponseDTO;
import com.example.features.fechas.models.EstadoFecha;
import com.example.features.fechas.services.interfaces.IFechaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Fechas", description = "Gestión de las fechas del torneo")
@RestController
@RequestMapping("/api/fechas")
@RequiredArgsConstructor

        public class FechaController {
        private final IFechaService fechaService;

        @Operation(summary = "Crear una fecha (solo ADMIN)")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Fecha creada correctamente"),
                @ApiResponse(responseCode = "400", description = "Ya existe una fecha con ese nombre",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        public FechaResponseDTO crear(@Valid @RequestBody FechaCreateDTO dto){
                return fechaService.crear(dto);
        }

        @Operation(summary = "Listar todas las fechas")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Fechas obtenidas correctamente")
        })
        @GetMapping
        public List<FechaResponseDTO> listar(){
                return fechaService.listar();
        }

        @Operation(summary = "Buscar una fecha por ID")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Fecha encontrada correctamente"),
                @ApiResponse(responseCode = "404", description = "Fecha no encontrada",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @GetMapping("/{id}")
        public FechaResponseDTO buscarPorId(
                @Parameter(description = "ID de la fecha", example = "1")
                @PathVariable Long id){
                return fechaService.buscarPorId(id);
        }

        @Operation(summary = "Listar fechas filtradas por estado")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Fechas filtradas correctamente")
        })
        @GetMapping("/estado/{estado}")
        public List<FechaResponseDTO> listarPorEstado(
                @Parameter(description = "Estado de la fecha", example = "PROGRAMADA")
                @PathVariable EstadoFecha estado){
                return fechaService.listarPorEstado(estado);
        }

        @Operation(summary = "Actualizar una fecha (solo ADMIN)")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Fecha actualizada correctamente"),
                @ApiResponse(responseCode = "400", description = "La fecha no está en estado PROGRAMADA, o ya tiene partidos asociados",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Fecha no encontrada",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public FechaResponseDTO actualizar(
                @Parameter(description = "ID de la fecha", example = "1")
                @PathVariable Long id, @Valid @RequestBody FechaUpdateDTO dto){
                return fechaService.actualizar(id, dto);
        }

        @Operation(summary = "Eliminar una fecha (solo ADMIN)")
        @ApiResponses({
                @ApiResponse(responseCode = "204", description = "Fecha eliminada correctamente"),
                @ApiResponse(responseCode = "400", description = "La fecha tiene partidos asociados, o no está en estado PROGRAMADA",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Fecha no encontrada",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> eliminar(
                @Parameter(description = "ID de la fecha", example = "1")
                @PathVariable Long id){
        fechaService.eliminar(id);
        return ResponseEntity.noContent().build();
        }
}