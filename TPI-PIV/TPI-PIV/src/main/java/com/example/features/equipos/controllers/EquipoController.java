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
import com.example.features.equipos.dtos.request.EquipoCreateDTO;
import com.example.features.equipos.dtos.request.EquipoUpdateDTO;
import com.example.features.equipos.dtos.response.EquipoResponseDTO;
import com.example.features.equipos.services.impl.EquipoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/equipos")
@RequiredArgsConstructor

public class EquipoController {
    private final EquipoService equipoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipoResponseDTO> crear(@Valid @RequestBody EquipoCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(equipoService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<EquipoResponseDTO>> listar() {
        return ResponseEntity.ok(equipoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(equipoService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        equipoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipoResponseDTO> actualizarEquipo(@PathVariable Long id, @RequestBody EquipoUpdateDTO dto) {
        return ResponseEntity.ok(equipoService.actualizarEquipo(id, dto));
    }
}
