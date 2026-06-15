package com.example.features.fechas.controllers;

import com.example.features.fechas.dtos.request.FechaCreateDTO;
import com.example.features.fechas.dtos.request.FechaUpdateDTO;
import com.example.features.fechas.dtos.response.FechaResponseDTO;
import com.example.features.fechas.models.EstadoFecha;
import com.example.features.fechas.services.interfaces.IFechaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/fechas")
@RequiredArgsConstructor

public class FechaController {
    private final IFechaService fechaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public FechaResponseDTO crear(@Valid @RequestBody FechaCreateDTO dto){
        return fechaService.crear(dto);
    }

    @GetMapping
    public List<FechaResponseDTO> listar(){
        return fechaService.listar();
    }

    @GetMapping("/{id}")
    public FechaResponseDTO buscarPorId(@PathVariable Long id){
        return fechaService.buscarPorId(id);
    }

    @GetMapping("/estado/{estado}")
    public List<FechaResponseDTO> listarPorEstado(@PathVariable EstadoFecha estado){
        return fechaService.listarPorEstado(estado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public FechaResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody FechaUpdateDTO dto){
        return fechaService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Long id){
        fechaService.eliminar(id);
    }
}