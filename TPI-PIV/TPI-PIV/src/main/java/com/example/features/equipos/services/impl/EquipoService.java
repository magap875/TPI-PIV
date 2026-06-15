package com.example.features.equipos.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.config.exceptions.BadRequestException;
import com.example.config.exceptions.ResourceNotFoundException;
import com.example.features.equipos.dtos.request.EquipoCreateDTO;
import com.example.features.equipos.dtos.request.EquipoUpdateDTO;
import com.example.features.equipos.dtos.response.EquipoResponseDTO;
import com.example.features.equipos.mappers.EquipoMapper;
import com.example.features.equipos.models.Equipo;
import com.example.features.equipos.repositories.EquipoRepository;
import com.example.features.equipos.services.interfaces.IEquipoService;
import com.example.features.partidos.repositories.PartidoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EquipoService implements IEquipoService {

    private final EquipoRepository equipoRepository;
    private final PartidoRepository partidoRepository;

    @Override
    public EquipoResponseDTO crear(EquipoCreateDTO dto) {
        if (equipoRepository.existsByNombreIgnoreCase(dto.nombre())) {
            throw new BadRequestException("Ya existe un equipo con ese nombre.");
        }

        Equipo equipo = Equipo.builder()
                .nombre(dto.nombre())
                .build();

        equipoRepository.save(equipo);
        return EquipoMapper.toResponseDTO(equipo);
    }

    @Override
    public List<EquipoResponseDTO> listar() {
        return equipoRepository.findAll()
                .stream()
                .map(EquipoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public EquipoResponseDTO buscarPorId(Long id) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado."));
        return EquipoMapper.toResponseDTO(equipo);
    }

    @Override
    public void eliminar(Long id) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado."));
        if (partidoRepository.existsByEquipoLocalIdOrEquipoVisitanteId(id, id)){
            throw new BadRequestException("No se puede eliminar un equipo que tiene partidos asociados.");
        }
        equipoRepository.delete(equipo);
    }

    @Override
    public EquipoResponseDTO actualizarEquipo(Long id, EquipoUpdateDTO dto) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado."));

        equipo.setNombre(dto.nombre());
        Equipo actualizado = equipoRepository.save(equipo);
        return EquipoMapper.toResponseDTO(actualizado);
    }
}
