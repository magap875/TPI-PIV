package com.example.features.partidos.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.config.response.BaseResponse;
import com.example.config.exceptions.ErrorResponseDTO;
import com.example.features.partidos.dtos.request.PartidoCreateDTO;
import com.example.features.partidos.dtos.request.PartidoResultadoDTO;
import com.example.features.partidos.dtos.request.PartidoUpdateDTO;
import com.example.features.partidos.dtos.response.PartidoResponseDTO;
import com.example.features.partidos.models.EstadoPartido;
import com.example.features.partidos.services.interfaces.IPartidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Partidos", description = "Gestión de partidos del torneo")
@RestController
@RequestMapping("/api/partidos")
@RequiredArgsConstructor

public class PartidoController {
        private final IPartidoService partidoService;

        @Operation(summary = "Crear un partido (solo ADMIN)")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Partido creado correctamente"),
                @ApiResponse(responseCode = "400", description = "Los equipos no pueden ser iguales",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Equipo local, equipo visitante o fecha no encontrados",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<BaseResponse<PartidoResponseDTO>> crear(@RequestBody PartidoCreateDTO dto){
                return ResponseEntity.ok(BaseResponse.ok(partidoService.crearPartido(dto), "Partido creado correctamente"));
        }

        @Operation(summary = "Listar todos los partidos")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Partidos obtenidos correctamente")
        })
        @GetMapping
        public ResponseEntity<BaseResponse<List<PartidoResponseDTO>>> listar(){
                return ResponseEntity.ok(BaseResponse.ok(partidoService.listarPartidos(), "Partidos obtenidos correctamente"));
        }

        @Operation(summary = "Buscar un partido por ID")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Partido obtenido correctamente"),
                @ApiResponse(responseCode = "404", description = "Partido no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @GetMapping("/{id}")
        public ResponseEntity<BaseResponse<PartidoResponseDTO>> buscar(
                @Parameter(description = "ID del partido", example = "1")
                @PathVariable Long id) {
                return ResponseEntity.ok(BaseResponse.ok(partidoService.buscarPorId(id), "Partido obtenido correctamente"));
        }

        @Operation(summary = "Actualizar un partido (solo ADMIN)")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Partido actualizado correctamente"),
                @ApiResponse(responseCode = "400", description = "El partido no está en estado POR_JUGARSE, o los equipos son iguales",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Partido, equipo local o equipo visitante no encontrados",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<BaseResponse<PartidoResponseDTO>> actualizar(
                @Parameter(description = "ID del partido", example = "1")
                @PathVariable Long id, @RequestBody PartidoUpdateDTO dto){
                return ResponseEntity.ok(BaseResponse.ok(partidoService.actualizarPartido(id, dto), "Partido actualizado correctamente"));
        }

        @Operation(summary = "Listar los partidos de una fecha")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Partidos obtenidos correctamente (lista vacía si la fecha no tiene partidos o no existe)")
        })
        @GetMapping("/fecha/{fechaId}")
        public ResponseEntity<BaseResponse<List<PartidoResponseDTO>>> listarPorFecha(
                @Parameter(description = "ID de la fecha", example = "1")
                @PathVariable Long fechaId) {
                return ResponseEntity.ok(BaseResponse.ok(partidoService.listarPorFecha(fechaId),"Partidos obtenidos correctamente"));
        }

        @Operation(summary = "Cambiar el estado de un partido a EN_JUEGO (solo ADMIN)")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Partido iniciado correctamente"),
                @ApiResponse(responseCode = "400", description = "El partido no puede pasar a EN_JUEGO desde su estado actual",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Partido no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PatchMapping("/{id}/en-juego")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<BaseResponse<PartidoResponseDTO>> cambiarEstado(
                @Parameter(description = "ID del partido", example = "1")
                @PathVariable Long id){
                return ResponseEntity.ok(BaseResponse.ok(partidoService.cambiarEstadoEnJuego(id), "Partido iniciado correctamente"));
        }

        @Operation(summary = "Cargar el resultado final de un partido (solo ADMIN)")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Resultado cargado correctamente"),
                @ApiResponse(responseCode = "400", description = "El partido no está en estado EN_JUEGO",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Partido no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PatchMapping("/{id}/resultado")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<BaseResponse<PartidoResponseDTO>> cargarResultado(
                @Parameter(description = "ID del partido", example = "1")
                @PathVariable Long id, @RequestBody PartidoResultadoDTO dto){
                return ResponseEntity.ok(BaseResponse.ok(partidoService.cargarResultado(id, dto), "Resultado cargado correctamente"));
        }

        @Operation(summary = "Eliminar un partido (solo ADMIN)")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Partido eliminado correctamente"),
                @ApiResponse(responseCode = "400", description = "El partido no está en estado POR_JUGARSE, o ya tiene pronósticos asociados",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Partido no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<BaseResponse<Void>> eliminar(
                @Parameter(description = "ID del partido", example = "1")
                @PathVariable Long id){
                partidoService.eliminarPartido(id);
                return ResponseEntity.ok(BaseResponse.ok(null, "Partido eliminado correctamente"));
        }

        @Operation(summary = "Listar partidos filtrados por estado")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Partidos filtrados correctamente")
        })
        @GetMapping("/estado/{estado}")
        public ResponseEntity<BaseResponse<List<PartidoResponseDTO>>> listarPorEstado(
                @Parameter(description = "Estado del partido", example = "POR_JUGARSE")
                @PathVariable EstadoPartido estado){
                return ResponseEntity.ok(BaseResponse.ok(partidoService.listarPorEstado(estado),"Partidos filtrados correctamente"));
        }
}