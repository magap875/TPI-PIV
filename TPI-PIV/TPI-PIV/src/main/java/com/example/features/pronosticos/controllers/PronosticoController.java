package com.example.features.pronosticos.controllers;

import com.example.features.pronosticos.dtos.request.PronosticoRequestDTO;
import com.example.features.pronosticos.dtos.response.PronosticoResponseDTO;
import com.example.features.pronosticos.services.interfaces.IPronosticoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/pronosticos")
public class PronosticoController {
    private final IPronosticoService pronosticoService;

    // crear o actualizar pronóstico
    @PostMapping("/{partidoId}")
    public ResponseEntity<PronosticoResponseDTO> crearOActualizar(@PathVariable Long partidoId, @Valid @RequestBody PronosticoRequestDTO dto, Authentication authentication){
        return ResponseEntity.ok(pronosticoService.crearOActualizar(authentication.getName(),partidoId,dto)
        );
    }

    // listar pronósticos de un usuario en especifico
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PronosticoResponseDTO>> porUsuario(@PathVariable Long usuarioId){
        return ResponseEntity.ok(pronosticoService.listarPorUsuario(usuarioId));
    }

    // listar pronósticos de un partido en especifico
    @GetMapping("/partido/{partidoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PronosticoResponseDTO>> porPartido(@PathVariable Long partidoId) {
        return ResponseEntity.ok(pronosticoService.listarPorPartido(partidoId)
        );
    }
}