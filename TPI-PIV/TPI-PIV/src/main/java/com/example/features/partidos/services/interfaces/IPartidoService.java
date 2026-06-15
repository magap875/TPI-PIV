package com.example.features.partidos.services.interfaces;

import java.util.List;
import com.example.features.partidos.dtos.request.PartidoCreateDTO;
import com.example.features.partidos.dtos.request.PartidoUpdateDTO;
import com.example.features.partidos.dtos.request.PartidoResultadoDTO;
import com.example.features.partidos.dtos.response.PartidoResponseDTO;

public interface IPartidoService {
    PartidoResponseDTO crearPartido(PartidoCreateDTO dto);
    List<PartidoResponseDTO> listarPartidos();
    List<PartidoResponseDTO> listarPorFecha(Long fechaId);
    PartidoResponseDTO actualizarPartido(Long id, PartidoUpdateDTO dto);
    PartidoResponseDTO cambiarEstadoEnJuego(Long id);
    PartidoResponseDTO cargarResultado(Long id, PartidoResultadoDTO dto);
    PartidoResponseDTO buscarPorId(Long id);
    void eliminarPartido(Long id);
}