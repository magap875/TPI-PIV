package com.example.features.grupos.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.features.grupos.dtos.request.GrupoRequestDTO;
import com.example.features.grupos.dtos.response.GrupoResponseDTO;
import com.example.features.grupos.services.interfaces.IGrupoService;
import com.example.features.miembrosgrupos.dtos.request.UnirseGrupoRequestDTO;
import com.example.features.miembrosgrupos.dtos.response.MiembroGrupoResponseDTO;
import com.example.features.rankings.dtos.RankingResponseDTO;
import java.util.List;

@RestController
@RequestMapping("/api/grupos")
@AllArgsConstructor
public class GrupoController {
        private final IGrupoService grupoService;

        @PostMapping
        public ResponseEntity<GrupoResponseDTO> crearGrupo(@Valid @RequestBody GrupoRequestDTO dto, Authentication authentication){
                String emailUsuarioAutenticado = authentication.getName();

                return ResponseEntity.ok(grupoService.crearGrupo(dto, emailUsuarioAutenticado));
        }

        @PostMapping("/unirse")
        public ResponseEntity<MiembroGrupoResponseDTO> unirseAGrupo(@Valid @RequestBody UnirseGrupoRequestDTO dto, Authentication authentication){
                String emailUsuarioAutenticado = authentication.getName();

                return ResponseEntity.ok(grupoService.unirseAGrupo(dto, emailUsuarioAutenticado));
        }

        @GetMapping
        public ResponseEntity<List<GrupoResponseDTO>> listarGrupos(){
                return ResponseEntity.ok(grupoService.listarGrupos());
        }

        @GetMapping("/{id}")
        public ResponseEntity<GrupoResponseDTO> obtenerGrupoPorId(@PathVariable Long id){
                return ResponseEntity.ok(grupoService.obtenerGrupoPorId(id));
        }

        @GetMapping("/{grupoId}/miembros")
        public ResponseEntity<List<MiembroGrupoResponseDTO>> listarMiembrosDelGrupo(@PathVariable Long grupoId){
                return ResponseEntity.ok(grupoService.listarMiembrosDelGrupo(grupoId));
        }

        @GetMapping("/mis-grupos")
        public ResponseEntity<List<MiembroGrupoResponseDTO>> listarMisGrupos(Authentication authentication){
                String emailUsuarioAutenticado = authentication.getName();

                return ResponseEntity.ok(grupoService.listarGruposDelUsuario(emailUsuarioAutenticado));
        }

        @GetMapping("/{grupoId}/ranking")
        public ResponseEntity<List<RankingResponseDTO>> obtenerRankingGrupo(@PathVariable Long grupoId){
                return ResponseEntity.ok(grupoService.obtenerRankingGrupo(grupoId));
        }

        @DeleteMapping("/{grupoId}/salir")
        public ResponseEntity<Void> salirDelGrupo(@PathVariable Long grupoId, Authentication authentication){
                String emailUsuarioAutenticado = authentication.getName();

                grupoService.salirDelGrupo(grupoId, emailUsuarioAutenticado);
                return ResponseEntity.noContent().build();
        }
}
