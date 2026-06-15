package com.example.features.pronosticos.services.interfaces;

import com.example.features.pronosticos.dtos.request.PronosticoRequestDTO;
import com.example.features.pronosticos.dtos.response.PronosticoResponseDTO;
import java.util.List;

public interface IPronosticoService{
    PronosticoResponseDTO crearOActualizar(String username, Long partidoId, PronosticoRequestDTO dto);
    List<PronosticoResponseDTO> listarPorUsuario(Long usuarioId);
    List<PronosticoResponseDTO> listarPorPartido(Long partidoId);
    List<PronosticoResponseDTO> listarPorUsuarioEmail(String email);
}