package com.example.features.grupos.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.config.exceptions.ErrorResponseDTO;
import com.example.features.grupos.dtos.request.GrupoRequestDTO;
import com.example.features.grupos.dtos.response.GrupoResponseDTO;
import com.example.features.grupos.services.interfaces.IGrupoService;
import com.example.features.miembrosgrupos.dtos.request.UnirseGrupoRequestDTO;
import com.example.features.miembrosgrupos.dtos.response.MiembroGrupoResponseDTO;
import com.example.features.rankings.dtos.RankingResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "Grupos", description = "Creación y gestión de grupos de apuestas")
@RestController
@RequestMapping("/api/grupos")
@AllArgsConstructor
public class GrupoController {
        private final IGrupoService grupoService;

        @Operation(summary = "Crear un grupo")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Grupo creado correctamente"),
                @ApiResponse(responseCode = "404", description = "Usuario autenticado no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PostMapping
        public ResponseEntity<GrupoResponseDTO> crearGrupo(@Valid @RequestBody GrupoRequestDTO dto, Authentication authentication){
                String emailUsuarioAutenticado = authentication.getName();
                return ResponseEntity.ok(grupoService.crearGrupo(dto, emailUsuarioAutenticado));
        }

        @Operation(summary = "Unirse a un grupo mediante código de invitación")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Unión al grupo realizada correctamente"),
                @ApiResponse(responseCode = "400", description = "El usuario ya pertenece a este grupo",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Usuario no encontrado, o código de invitación inválido",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PostMapping("/unirse")
        public ResponseEntity<MiembroGrupoResponseDTO> unirseAGrupo(@Valid @RequestBody UnirseGrupoRequestDTO dto, Authentication authentication){
                String emailUsuarioAutenticado = authentication.getName();
                return ResponseEntity.ok(grupoService.unirseAGrupo(dto, emailUsuarioAutenticado));
        }

        @Operation(summary = "Listar todos los grupos")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Grupos obtenidos correctamente")
        })
        @GetMapping
        public ResponseEntity<List<GrupoResponseDTO>> listarGrupos(){
                return ResponseEntity.ok(grupoService.listarGrupos());
        }

        @Operation(summary = "Buscar un grupo por ID")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Grupo encontrado correctamente"),
                @ApiResponse(responseCode = "404", description = "Grupo no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @GetMapping("/{id}")
        public ResponseEntity<GrupoResponseDTO> obtenerGrupoPorId(
                @Parameter(description = "ID del grupo", example = "1")
                @PathVariable Long id){
                return ResponseEntity.ok(grupoService.obtenerGrupoPorId(id));
        }

        @Operation(summary = "Listar los miembros de un grupo")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Miembros obtenidos correctamente"),
                @ApiResponse(responseCode = "404", description = "Grupo no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @GetMapping("/{grupoId}/miembros")
        public ResponseEntity<List<MiembroGrupoResponseDTO>> listarMiembrosDelGrupo(
                @Parameter(description = "ID del grupo", example = "1")
                @PathVariable Long grupoId){
                return ResponseEntity.ok(grupoService.listarMiembrosDelGrupo(grupoId));
        }

        @Operation(summary = "Listar los grupos a los que pertenece el usuario autenticado")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Grupos del usuario obtenidos correctamente"),
                @ApiResponse(responseCode = "404", description = "Usuario autenticado no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @GetMapping("/mis-grupos")
        public ResponseEntity<List<MiembroGrupoResponseDTO>> listarMisGrupos(Authentication authentication){
                String emailUsuarioAutenticado = authentication.getName();
                return ResponseEntity.ok(grupoService.listarGruposDelUsuario(emailUsuarioAutenticado));
        }

        @Operation(summary = "Obtener el ranking de un grupo")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Ranking obtenido correctamente"),
                @ApiResponse(responseCode = "404", description = "Grupo no encontrado",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @GetMapping("/{grupoId}/ranking")
        public ResponseEntity<List<RankingResponseDTO>> obtenerRankingGrupo(
                @Parameter(description = "ID del grupo", example = "1")
                @PathVariable Long grupoId){
                return ResponseEntity.ok(grupoService.obtenerRankingGrupo(grupoId));
        }

        @Operation(summary = "Salir de un grupo")
        @ApiResponses({
                @ApiResponse(responseCode = "204", description = "El usuario abandonó el grupo correctamente"),
                @ApiResponse(responseCode = "404", description = "Usuario no encontrado, o el usuario no pertenece a este grupo",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @DeleteMapping("/{grupoId}/salir")
        public ResponseEntity<Void> salirDelGrupo(
                @Parameter(description = "ID del grupo", example = "1")
                @PathVariable Long grupoId, Authentication authentication){
                String emailUsuarioAutenticado = authentication.getName();
                grupoService.salirDelGrupo(grupoId, emailUsuarioAutenticado);
                return ResponseEntity.noContent().build();
        }
}