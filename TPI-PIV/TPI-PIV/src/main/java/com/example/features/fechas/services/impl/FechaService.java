package com.example.features.fechas.services.impl;

import com.example.config.exceptions.BadRequestException;
import com.example.config.exceptions.ResourceNotFoundException;
import com.example.features.fechas.dtos.request.FechaCreateDTO;
import com.example.features.fechas.dtos.request.FechaUpdateDTO;
import com.example.features.fechas.dtos.response.FechaResponseDTO;
import com.example.features.fechas.mappers.FechaMapper;
import com.example.features.fechas.models.Fecha;
import com.example.features.fechas.models.EstadoFecha;
import com.example.features.fechas.repositories.FechaRepository;
import com.example.features.fechas.services.interfaces.IFechaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor

public class FechaService implements IFechaService {
    private final FechaRepository fechaRepository;

    @Override
    public FechaResponseDTO crear(FechaCreateDTO dto) {
        if (fechaRepository.existsByNombre(dto.nombre())) {
            throw new BadRequestException("Ya existe una fecha con ese nombre");
        }

        Fecha fecha = FechaMapper.toEntity(dto);
        Fecha guardada = fechaRepository.save(fecha);

        return FechaMapper.toResponseDTO(guardada);
    }

    @Override
    public List<FechaResponseDTO> listar() {
        return fechaRepository.findAll()
                .stream()
                .map(FechaMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<FechaResponseDTO> listarPorEstado(EstadoFecha estado) {
        return fechaRepository.findByEstado(estado)
                .stream()
                .map(FechaMapper::toResponseDTO)
                .toList();
    }

    @Override
    public FechaResponseDTO buscarPorId(Long id) {
        Fecha fecha = fechaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fecha no encontrada"));

        return FechaMapper.toResponseDTO(fecha);
    }

    // No se pueden modificar los datos de una fecha si no está en estado PROGRAMADA
    // o si tiene partidos asociados
    @Override
    public FechaResponseDTO actualizar(Long id, FechaUpdateDTO dto) {
        Fecha fecha = fechaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fecha no encontrada"));

        if (fecha.getEstado() != EstadoFecha.PROGRAMADA) {
            throw new BadRequestException("Solo se puede modificar una fecha PROGRAMADA");
        }

        if (!fecha.getPartidos().isEmpty()) {
            throw new BadRequestException("No se puede modificar una fecha con partidos asociados");
        }

        fecha.setNombre(dto.nombre());

        Fecha updated = fechaRepository.save(fecha);

        return FechaMapper.toResponseDTO(updated);
    }

    @Override
    public void eliminar(Long id) {
        Fecha fecha = fechaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fecha no encontrada"));

        if (!fecha.getPartidos().isEmpty()) {
            throw new BadRequestException("No se puede eliminar una fecha con partidos asociados");
        }

        if (fecha.getEstado() != EstadoFecha.PROGRAMADA) {
            throw new BadRequestException("Solo se puede eliminar una fecha PROGRAMADA");
        }

        fechaRepository.delete(fecha);
    }
}