package com.example.features.partidos.services.impl;

import com.example.config.exceptions.BadRequestException;
import com.example.config.exceptions.ResourceNotFoundException;
import com.example.features.equipos.models.Equipo;
import com.example.features.equipos.repositories.EquipoRepository;
import com.example.features.fechas.models.EstadoFecha;
import com.example.features.fechas.models.Fecha;
import com.example.features.fechas.repositories.FechaRepository;
import com.example.features.partidos.dtos.request.PartidoCreateDTO;
import com.example.features.partidos.dtos.request.PartidoUpdateDTO;
import com.example.features.partidos.dtos.request.PartidoResultadoDTO;
import com.example.features.partidos.dtos.response.PartidoResponseDTO;
import com.example.features.partidos.mappers.PartidoMapper;
import com.example.features.partidos.models.EstadoPartido;
import com.example.features.partidos.models.Partido;
import com.example.features.partidos.repositories.PartidoRepository;
import com.example.features.partidos.services.interfaces.IPartidoService;
import com.example.features.pronosticos.models.Pronostico;
import com.example.features.pronosticos.models.ResultadoTendencia;
import com.example.features.users.models.Usuario;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional

public class PartidoService implements IPartidoService {
    private final PartidoRepository partidoRepository;
    private final EquipoRepository equipoRepository;
    private final FechaRepository fechaRepository;

    @Override
    public PartidoResponseDTO crearPartido(PartidoCreateDTO dto){
        if (dto.equipoLocalId().equals(dto.equipoVisitanteId())) {
            throw new BadRequestException("Los equipos no pueden ser iguales");
        }

        Equipo local = equipoRepository.findById(dto.equipoLocalId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo local no encontrado"));

        Equipo visitante = equipoRepository.findById(dto.equipoVisitanteId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo visitante no encontrado"));

        Fecha fecha = fechaRepository.findById(dto.fechaId())
                .orElseThrow(() -> new ResourceNotFoundException("Fecha no encontrada"));

        Partido partido = new Partido();
        partido.setEquipoLocal(local);
        partido.setEquipoVisitante(visitante);
        partido.setFecha(fecha);
        partido.setFechaHorarioInicio(dto.fechaHorarioInicio());
        partido.setEstado(EstadoPartido.POR_JUGARSE);

        return PartidoMapper.toResponseDTO(partidoRepository.save(partido));
    }

    @Override
    public List<PartidoResponseDTO> listarPartidos(){
        return partidoRepository.findAll()
                .stream()
                .map(PartidoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<PartidoResponseDTO> listarPorFecha(Long fechaId){
        return partidoRepository.findByFechaId(fechaId)
                .stream()
                .map(PartidoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<PartidoResponseDTO> listarPorEstado(EstadoPartido estado){
        List<Partido> partidos = partidoRepository.findByEstado(estado);
        return partidos.stream()
                .map(PartidoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public PartidoResponseDTO actualizarPartido(Long id, PartidoUpdateDTO dto){
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));

        if (partido.getEstado() != EstadoPartido.POR_JUGARSE) {
            throw new BadRequestException("Solo se puede modificar un partido con estado POR_JUGARSE");
        }

        if (dto.equipoLocalId().equals(dto.equipoVisitanteId())) {
            throw new BadRequestException("Los equipos no pueden ser iguales");
        }

        Equipo local = equipoRepository.findById(dto.equipoLocalId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo local no encontrado"));

        Equipo visitante = equipoRepository.findById(dto.equipoVisitanteId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo visitante no encontrado"));

        partido.setEquipoLocal(local);
        partido.setEquipoVisitante(visitante);
        partido.setFechaHorarioInicio(dto.fechaHorarioInicio());

        return PartidoMapper.toResponseDTO(partidoRepository.save(partido));
    }


    @Override
    public PartidoResponseDTO cambiarEstadoEnJuego(Long id){
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));

        if (partido.getEstado() != EstadoPartido.POR_JUGARSE) {
            throw new BadRequestException("El partido no puede pasar a EN_JUEGO");
        }

        partido.setEstado(EstadoPartido.EN_JUEGO);
    
        Partido saved = partidoRepository.save(partido);
        Fecha fecha = saved.getFecha();
        actualizarEstadoFecha(fecha);
        fechaRepository.save(fecha);

        return PartidoMapper.toResponseDTO(saved);
    }

    @Override
    public PartidoResponseDTO cargarResultado(Long id, PartidoResultadoDTO dto){
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));

        if (partido.getEstado() != EstadoPartido.EN_JUEGO) {
            throw new BadRequestException("Solo se puede cargar resultado en estado EN_JUEGO");
        }

        partido.setGolesLocal(dto.golesLocal());
        partido.setGolesVisitante(dto.golesVisitante());

        if (dto.golesLocal() > dto.golesVisitante()) {
            partido.setResultadoTendencia(ResultadoTendencia.LOCAL);
        } else if (dto.golesLocal() < dto.golesVisitante()) {
            partido.setResultadoTendencia(ResultadoTendencia.VISITANTE);
        } else {
            partido.setResultadoTendencia(ResultadoTendencia.EMPATE);
        }

        partido.setEstado(EstadoPartido.FINALIZADO);
        Partido saved = partidoRepository.save(partido);
        Fecha fecha = saved.getFecha();
        actualizarEstadoFecha(fecha);
        fechaRepository.save(fecha);
        calcularPuntos(saved);

        return PartidoMapper.toResponseDTO(saved);
    }

    @Override
    public PartidoResponseDTO buscarPorId(Long id){
        return partidoRepository.findById(id)
                .map(PartidoMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));
    }

    @Override
    public void eliminarPartido(Long id){
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));

        if (partido.getEstado() != EstadoPartido.POR_JUGARSE) {
            throw new BadRequestException("Solo se pueden eliminar partidos POR_JUGARSE");
        }
        if (!partido.getPronosticos().isEmpty()) {
            throw new BadRequestException("No se puede eliminar un partido con pronósticos");
        }
        partidoRepository.delete(partido);
    }

    // metodos helpers
    private void actualizarEstadoFecha(Fecha fecha){
        List<Partido> partidos = fecha.getPartidos();

        boolean todosFinalizados = partidos.stream().allMatch(p -> p.getEstado() == EstadoPartido.FINALIZADO);
        boolean algunoEnJuego = partidos.stream().anyMatch(p -> p.getEstado() == EstadoPartido.EN_JUEGO);
        boolean todosPorJugar = partidos.stream().allMatch(p -> p.getEstado() == EstadoPartido.POR_JUGARSE);

        if (todosFinalizados) {
                fecha.setEstado(EstadoFecha.FINALIZADA);
            } else if (algunoEnJuego) {
                fecha.setEstado(EstadoFecha.EN_JUEGO);
            } else if (todosPorJugar) {
                fecha.setEstado(EstadoFecha.PROGRAMADA);
            }
    }

    private void calcularPuntos(Partido partido) {
        List<Pronostico> pronosticos = partido.getPronosticos();

            for (Pronostico p : pronosticos) {
                Usuario usuario = p.getUsuario();
                int puntos = 0;
                boolean exacto =
                        p.getGolesLocalPronosticados().equals(partido.getGolesLocal()) &&
                        p.getGolesVisitantePronosticados().equals(partido.getGolesVisitante());

                if (exacto) { 
                    puntos = 3;
                    usuario.setCantidadResultadosExactos(usuario.getCantidadResultadosExactos() + 1);
                } else if (mismaTendencia(p, partido)) {
                    puntos = 1;
                }

                p.setPuntosObtenidos(puntos);
                usuario.setPuntosTotales(usuario.getPuntosTotales() + puntos);
                }
    }

    private boolean mismaTendencia(Pronostico p, Partido partido){
        ResultadoTendencia predicho;

        if (p.getGolesLocalPronosticados() > p.getGolesVisitantePronosticados()) {
            predicho = ResultadoTendencia.LOCAL;
        } else if (p.getGolesLocalPronosticados() < p.getGolesVisitantePronosticados()) {
            predicho = ResultadoTendencia.VISITANTE;
        } else {
            predicho = ResultadoTendencia.EMPATE;
        }

        return predicho == partido.getResultadoTendencia();
    }
}