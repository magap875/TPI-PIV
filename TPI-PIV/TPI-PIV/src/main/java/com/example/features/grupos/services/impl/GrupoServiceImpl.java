package com.example.features.grupos.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.features.grupos.dtos.request.GrupoRequestDTO;
import com.example.features.grupos.dtos.response.GrupoResponseDTO;
import com.example.features.grupos.mappers.GrupoMapper;
import com.example.features.grupos.models.Grupo;
import com.example.features.grupos.repositories.GrupoRepository;
import com.example.features.grupos.services.interfaces.IGrupoService;
import com.example.features.miembrosgrupos.dtos.request.UnirseGrupoRequestDTO;
import com.example.features.miembrosgrupos.dtos.response.MiembroGrupoResponseDTO;
import com.example.features.miembrosgrupos.mappers.MiembroGrupoMapper;
import com.example.features.miembrosgrupos.models.MiembroGrupo;
import com.example.features.miembrosgrupos.repositories.MiembroGrupoRepository;
import com.example.features.users.dtos.response.UsuarioResponseDTO;
import com.example.features.users.mappers.UsuarioMapper;
import com.example.features.users.models.Usuario;
import com.example.features.users.repositories.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GrupoServiceImpl implements IGrupoService {

    private final GrupoRepository grupoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MiembroGrupoRepository miembroGrupoRepository;

    @Override
    public GrupoResponseDTO crearGrupo(GrupoRequestDTO dto, String emailUsuarioAutenticado) {

        Usuario usuarioCreador = usuarioRepository.findByEmail(emailUsuarioAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Grupo grupo = new Grupo();
        grupo.setNombre(dto.nombre());
        grupo.setCodigoInvitacion(generarCodigoInvitacion());

        Grupo grupoGuardado = grupoRepository.save(grupo);

        MiembroGrupo miembro = new MiembroGrupo();
        miembro.setUsuario(usuarioCreador);
        miembro.setGrupo(grupoGuardado);
        miembro.setFechaIngreso(LocalDateTime.now());

        miembroGrupoRepository.save(miembro);

        return GrupoMapper.toResponseDTO(grupoGuardado);
    }

    @Override
    public MiembroGrupoResponseDTO unirseAGrupo(UnirseGrupoRequestDTO dto, String emailUsuarioAutenticado) {

        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Grupo grupo = grupoRepository.findByCodigoInvitacion(dto.codigoInvitacion())
                .orElseThrow(() -> new RuntimeException("Código de invitación inválido"));

        boolean yaEsMiembro = miembroGrupoRepository.existsByUsuarioIdAndGrupoId(
                usuario.getId(),
                grupo.getId());

        if (yaEsMiembro) {
            throw new RuntimeException("El usuario ya pertenece a este grupo");
        }

        MiembroGrupo miembro = new MiembroGrupo();
        miembro.setUsuario(usuario);
        miembro.setGrupo(grupo);
        miembro.setFechaIngreso(LocalDateTime.now());

        MiembroGrupo guardado = miembroGrupoRepository.save(miembro);

        return MiembroGrupoMapper.toResponseDTO(guardado);
    }

    @Override
    public List<GrupoResponseDTO> listarGrupos() {
        return grupoRepository.findAll()
                .stream()
                .map(GrupoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public GrupoResponseDTO obtenerGrupoPorId(Long id) {

        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        return GrupoMapper.toResponseDTO(grupo);
    }

    @Override
    public List<MiembroGrupoResponseDTO> listarMiembrosDelGrupo(Long grupoId) {

        if (!grupoRepository.existsById(grupoId)) {
            throw new RuntimeException("Grupo no encontrado");
        }

        return miembroGrupoRepository.findByGrupoId(grupoId)
                .stream()
                .map(MiembroGrupoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<MiembroGrupoResponseDTO> listarGruposDelUsuario(String emailUsuarioAutenticado) {

        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return miembroGrupoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(MiembroGrupoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<UsuarioResponseDTO> rankingGrupo(Long grupoId) {

        if (!grupoRepository.existsById(grupoId)) {
            throw new RuntimeException("Grupo no encontrado");
        }

        return miembroGrupoRepository.findByGrupoId(grupoId)
                .stream()
                .map(MiembroGrupo::getUsuario)
                .sorted((u1, u2) -> {
                    int puntos = u2.getPuntosTotales().compareTo(u1.getPuntosTotales());

                    if (puntos != 0) {
                        return puntos;
                    }

                    return u2.getCantidadResultadosExactos()
                            .compareTo(u1.getCantidadResultadosExactos());
                })
                .map(UsuarioMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void salirDelGrupo(Long grupoId,String emailUsuarioAutenticado) {

        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        MiembroGrupo miembro = miembroGrupoRepository
                .findByUsuarioIdAndGrupoId(usuario.getId(), grupoId)
                .orElseThrow(() -> new RuntimeException("El usuario no pertenece a este grupo"));

        miembroGrupoRepository.delete(miembro);
    }

    private String generarCodigoInvitacion() {

        String codigo;

        do {
            codigo = UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();

        } while (grupoRepository.existsByCodigoInvitacion(codigo));

        return codigo;
    }
}
