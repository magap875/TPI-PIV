package com.example.features.pronosticos.services.impl;

import com.example.features.partidos.models.Partido;
import com.example.features.partidos.models.ResultadoTendencia;
import com.example.config.exceptions.BadRequestException;
import com.example.config.exceptions.ResourceNotFoundException;
import com.example.features.partidos.models.EstadoPartido;
import com.example.features.partidos.repositories.PartidoRepository;
import com.example.features.pronosticos.dtos.request.PronosticoRequestDTO;
import com.example.features.pronosticos.dtos.response.PronosticoResponseDTO;
import com.example.features.pronosticos.mappers.PronosticoMapper;
import com.example.features.pronosticos.models.Pronostico;
import com.example.features.pronosticos.repositories.PronosticoRepository;
import com.example.features.pronosticos.services.interfaces.IPronosticoService;
import com.example.features.users.models.Usuario;
import com.example.features.users.repositories.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor

public class PronosticoService implements IPronosticoService {
    private final PronosticoRepository pronosticoRepository;
    private final PartidoRepository partidoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public PronosticoResponseDTO crearOActualizar(String username, Long partidoId, PronosticoRequestDTO dto) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));

        if (partido.getEstado() != EstadoPartido.POR_JUGARSE) {
            throw new BadRequestException("No se puede pronosticar este partido debido a su estado actual");
        }

        LocalDateTime limite = partido.getFechaHorarioInicio().minusMinutes(30);

        if (LocalDateTime.now().isAfter(limite)) {
            throw new BadRequestException("Ya expiró el tiempo para pronosticar");
        }

        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Pronostico pronostico = pronosticoRepository
                .findByUsuarioIdAndPartidoId(usuario.getId(), partidoId)
                .orElse(new Pronostico());

        pronostico.setUsuario(usuario);
        pronostico.setPartido(partido);
        pronostico.setGolesLocalPronosticados(dto.golesLocalPronosticados());
        pronostico.setGolesVisitantePronosticados(dto.golesVisitantePronosticados());

        // solo setear fecha si es nuevo el pronostico
        if (pronostico.getId() == null) {
            pronostico.setFechaCreacion(LocalDateTime.now());
        }

        if (dto.golesLocalPronosticados() > dto.golesVisitantePronosticados()){
            pronostico.setResultadoTendencia(ResultadoTendencia.LOCAL);
        } else if (dto.golesLocalPronosticados() < dto.golesVisitantePronosticados()){
            pronostico.setResultadoTendencia(ResultadoTendencia.VISITANTE);
        } else {
            pronostico.setResultadoTendencia(ResultadoTendencia.EMPATE);
        }

        pronostico.setPuntosObtenidos(0);
        return PronosticoMapper.toResponseDTO(pronosticoRepository.save(pronostico));
    }

    @Override
    public List<PronosticoResponseDTO> listarPorUsuario(Long usuarioId){
        return pronosticoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(PronosticoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<PronosticoResponseDTO> listarPorUsuarioEmail(String email){
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return pronosticoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(PronosticoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public PronosticoResponseDTO obtenerPorUsuarioYPartido(String username, Long partidoId){
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return pronosticoRepository
                .findByUsuarioIdAndPartidoId(usuario.getId(), partidoId)
                .map(PronosticoMapper::toResponseDTO)
                .orElse(null);
    }

    @Override
    public List<PronosticoResponseDTO> listarPorPartido(Long partidoId) {
        return pronosticoRepository.findByPartidoId(partidoId)
                .stream()
                .map(PronosticoMapper::toResponseDTO)
                .toList();
    }
}