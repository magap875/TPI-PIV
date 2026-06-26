package com.example.features.grupos.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.config.exceptions.ResourceNotFoundException;
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
import com.example.features.pronosticos.repositories.PronosticoRepository;
import com.example.features.pronosticos.models.Pronostico;
import com.example.features.rankings.dtos.RankingResponseDTO;
import com.example.features.users.models.Usuario;
import com.example.features.users.repositories.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GrupoServiceImpl implements IGrupoService {
    private final GrupoRepository grupoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MiembroGrupoRepository miembroGrupoRepository;
    private final PronosticoRepository pronosticoRepository;
    @Override
    public GrupoResponseDTO crearGrupo(GrupoRequestDTO dto, String emailUsuarioAutenticado) {
        Usuario usuarioCreador = usuarioRepository.findByEmail(emailUsuarioAutenticado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Grupo grupo = grupoRepository.findByCodigoInvitacion(dto.codigoInvitacion())
                .orElseThrow(() -> new ResourceNotFoundException("Código de invitación inválido"));

        boolean yaEsMiembro = miembroGrupoRepository.existsByUsuarioIdAndGrupoId(
                usuario.getId(),
                grupo.getId());

        if (yaEsMiembro) {
            throw new ResourceNotFoundException("El usuario ya pertenece a este grupo");
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
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));

        return GrupoMapper.toResponseDTO(grupo);
    }

    @Override
    public List<MiembroGrupoResponseDTO> listarMiembrosDelGrupo(Long grupoId) {
        if (!grupoRepository.existsById(grupoId)) {
            throw new ResourceNotFoundException("Grupo no encontrado");
        }

        return miembroGrupoRepository.findByGrupoId(grupoId)
                .stream()
                .map(MiembroGrupoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<MiembroGrupoResponseDTO> listarGruposDelUsuario(String emailUsuarioAutenticado) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioAutenticado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return miembroGrupoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(MiembroGrupoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<RankingResponseDTO> rankingGrupo(Long grupoId) {

        if (!grupoRepository.existsById(grupoId)) {
            throw new ResourceNotFoundException("Grupo no encontrado");
        }

        Map<Long, LocalDateTime> primeraFechaPorUsuario = pronosticoRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        p -> p.getUsuario().getId(),
                        Collectors.mapping(
                                Pronostico::getFechaCreacion,
                                Collectors.minBy(LocalDateTime::compareTo))))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().orElse(LocalDateTime.MAX)));

        return miembroGrupoRepository.findByGrupoId(grupoId)
                .stream()
                .map(MiembroGrupo::getUsuario)
                .sorted(
                        Comparator
                                .comparing(Usuario::getPuntosTotales).reversed()
                                .thenComparing(
                                        Usuario::getCantidadResultadosExactos,
                                        Comparator.reverseOrder())
                                .thenComparing(u -> primeraFechaPorUsuario.getOrDefault(
                                        u.getId(),
                                        LocalDateTime.MAX)))
                .map(u -> new RankingResponseDTO(
                        u.getId(),
                        u.getNombre(),
                        u.getPuntosTotales(),
                        u.getCantidadResultadosExactos()))
                .toList();
    }

    @Override
    public void salirDelGrupo(Long grupoId, String emailUsuarioAutenticado) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioAutenticado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        MiembroGrupo miembro = miembroGrupoRepository
                .findByUsuarioIdAndGrupoId(usuario.getId(), grupoId)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no pertenece a este grupo"));

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
