// package com.example.features.rankings.services.impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.example.features.pronosticos.models.Pronostico;
import com.example.features.pronosticos.repositories.PronosticoRepository;
import com.example.features.rankings.dtos.RankingResponseDTO;
import com.example.features.rankings.services.interfaces.IRankingService;
import com.example.features.users.models.Usuario;
import com.example.features.users.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class RankingService implements IRankingService {
    private final UsuarioRepository usuarioRepository;
    private final PronosticoRepository pronosticoRepository;

    @Override
    public List<RankingResponseDTO> obtenerRankingGlobal() {

        Map<Long, LocalDateTime> primeraFechaPorUsuario =
                pronosticoRepository.findAll()
                        .stream()
                        .collect(Collectors.groupingBy(
                                p -> p.getUsuario().getId(),
                                Collectors.mapping(
                                        Pronostico::getFechaCreacion,
                                        Collectors.minBy(LocalDateTime::compareTo)
                                )
                        ))
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().orElse(LocalDateTime.MAX)
                        ));

        return usuarioRepository.findAll()
                .stream()
                .sorted(
                        Comparator
                                .comparing(Usuario::getPuntosTotales).reversed()
                                .thenComparing(Usuario::getCantidadResultadosExactos, Comparator.reverseOrder())
                                .thenComparing(u ->
                                        primeraFechaPorUsuario.getOrDefault(u.getId(), LocalDateTime.MAX)
                                )
                )
                .map(u -> new RankingResponseDTO(
                        u.getId(),
                        u.getNombre(),
                        u.getPuntosTotales(),
                        u.getCantidadResultadosExactos()
                ))
                .toList();
    }
}