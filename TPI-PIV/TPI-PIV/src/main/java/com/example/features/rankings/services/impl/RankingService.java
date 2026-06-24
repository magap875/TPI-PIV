// package com.example.features.rankings.services.impl;

// import lombok.AllArgsConstructor;

// import org.springframework.stereotype.Service;

// import com.example.config.exceptions.ResourceNotFoundException;
// import com.example.features.grupos.repositories.GrupoRepository;
// import com.example.features.rankings.dtos.RankingResponseDTO;
// import com.example.features.rankings.services.interfaces.IRankingService;
// import com.example.features.users.repositories.UsuarioRepository;

// import java.util.List;

// @Service
// @AllArgsConstructor
// public class RankingService implements IRankingService {

//     private final UsuarioRepository usuarioRepository;
//     private final GrupoRepository grupoRepository;

//     @Override
//     public List<RankingResponseDTO> rankingGlobal() {
//         return usuarioRepository.obtenerRankingGlobal();
//     }

//     @Override
//     public List<RankingResponseDTO> rankingGrupo(Long grupoId) {

//         if (!grupoRepository.existsById(grupoId)) {
//             throw new ResourceNotFoundException("Grupo no encontrado");
//         }

//         return usuarioRepository.obtenerRankingGrupo(grupoId);
//     }
// }