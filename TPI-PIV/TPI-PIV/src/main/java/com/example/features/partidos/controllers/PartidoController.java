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
import com.example.features.partidos.dtos.request.PartidoCreateDTO;
import com.example.features.partidos.dtos.request.PartidoResultadoDTO;
import com.example.features.partidos.dtos.request.PartidoUpdateDTO;
import com.example.features.partidos.dtos.response.PartidoResponseDTO;
import com.example.features.partidos.services.interfaces.IPartidoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/partidos")
@RequiredArgsConstructor

public class PartidoController {
        private final IPartidoService partidoService;

        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<BaseResponse<PartidoResponseDTO>> crear(@RequestBody PartidoCreateDTO dto){
                return ResponseEntity.ok(BaseResponse.ok(partidoService.crearPartido(dto), "Partido creado correctamente"));
        }

        @GetMapping
        public ResponseEntity<BaseResponse<List<PartidoResponseDTO>>> listar(){
                return ResponseEntity.ok(BaseResponse.ok(partidoService.listarPartidos(), "Partidos obtenidos correctamente"));
        }

        @GetMapping("/{id}")
        public ResponseEntity<BaseResponse<PartidoResponseDTO>> buscar(@PathVariable Long id) {
                return ResponseEntity.ok(BaseResponse.ok(partidoService.buscarPorId(id), "Partido obtenido correctamente"));
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<BaseResponse<PartidoResponseDTO>> actualizar(@PathVariable Long id, @RequestBody PartidoUpdateDTO dto){
                return ResponseEntity.ok(BaseResponse.ok(partidoService.actualizarPartido(id, dto), "Partido actualizado correctamente"));
        }

        @PatchMapping("/{id}/en-juego")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<BaseResponse<PartidoResponseDTO>> cambiarEstado(@PathVariable Long id){
                return ResponseEntity.ok(BaseResponse.ok(partidoService.cambiarEstadoEnJuego(id), "Partido iniciado correctamente"));
        }

        @PatchMapping("/{id}/resultado")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<BaseResponse<PartidoResponseDTO>> cargarResultado(@PathVariable Long id, @RequestBody PartidoResultadoDTO dto){
                return ResponseEntity.ok(BaseResponse.ok(partidoService.cargarResultado(id, dto), "Resultado cargado correctamente"));
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<BaseResponse<Void>> eliminar(@PathVariable Long id){
                partidoService.eliminarPartido(id);
                return ResponseEntity.ok(BaseResponse.ok(null, "Partido eliminado correctamente"));
        }
}