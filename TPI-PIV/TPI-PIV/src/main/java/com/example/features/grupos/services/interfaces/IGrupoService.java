package com.example.features.grupos.services.interfaces;

import java.util.List;
import com.example.features.grupos.dtos.request.GrupoRequestDTO;
import com.example.features.grupos.dtos.response.GrupoResponseDTO;
import com.example.features.miembrosgrupos.dtos.request.UnirseGrupoRequestDTO;
import com.example.features.miembrosgrupos.dtos.response.MiembroGrupoResponseDTO;
import com.example.features.rankings.dtos.RankingResponseDTO;

public interface IGrupoService {
    GrupoResponseDTO crearGrupo(GrupoRequestDTO dto,String emailUsuarioAutenticado);
    MiembroGrupoResponseDTO unirseAGrupo(UnirseGrupoRequestDTO dto,String emailUsuarioAutenticado);
    List<GrupoResponseDTO> listarGrupos();
    GrupoResponseDTO obtenerGrupoPorId(Long id);
    List<MiembroGrupoResponseDTO> listarMiembrosDelGrupo(Long grupoId);
    List<MiembroGrupoResponseDTO> listarGruposDelUsuario(String emailUsuarioAutenticado);
    List<RankingResponseDTO> rankingGrupo(Long grupoId);
    void salirDelGrupo(Long grupoId,String emailUsuarioAutenticado);
}
