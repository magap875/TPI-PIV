package com.example.features.fechas.services.interfaces;

import com.example.features.fechas.dtos.request.FechaCreateDTO;
import com.example.features.fechas.dtos.request.FechaUpdateDTO;
import com.example.features.fechas.dtos.response.FechaResponseDTO;
import com.example.features.fechas.models.EstadoFecha;
import java.util.List;

public interface IFechaService {
    FechaResponseDTO crear(FechaCreateDTO dto);
    List<FechaResponseDTO> listar();
    List<FechaResponseDTO> listarPorEstado(EstadoFecha estado);
    FechaResponseDTO buscarPorId(Long id);
    FechaResponseDTO actualizar(Long id, FechaUpdateDTO dto);
    void eliminar(Long id);
}