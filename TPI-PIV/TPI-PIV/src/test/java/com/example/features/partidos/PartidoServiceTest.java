package com.example.features.partidos;

import com.example.config.exceptions.BadRequestException;
import com.example.config.exceptions.ResourceNotFoundException;
import com.example.features.equipos.models.Equipo;
import com.example.features.equipos.repositories.EquipoRepository;
import com.example.features.fechas.models.EstadoFecha;
import com.example.features.fechas.models.Fecha;
import com.example.features.fechas.repositories.FechaRepository;
import com.example.features.partidos.dtos.request.PartidoCreateDTO;
import com.example.features.partidos.dtos.request.PartidoResultadoDTO;
import com.example.features.partidos.dtos.request.PartidoUpdateDTO;
import com.example.features.partidos.models.EstadoPartido;
import com.example.features.partidos.models.Partido;
import com.example.features.partidos.repositories.PartidoRepository;
import com.example.features.partidos.services.impl.PartidoService;
import com.example.features.pronosticos.models.Pronostico;
import com.example.features.partidos.models.ResultadoTendencia;
import com.example.features.users.models.Rol;
import com.example.features.users.models.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartidoServiceTest {

    @Mock
    private PartidoRepository partidoRepository;

    @Mock
    private EquipoRepository equipoRepository;

    @Mock
    private FechaRepository fechaRepository;

    @InjectMocks
    private PartidoService partidoService;

    @Test
    void crearPartido_deberiaCrearConEstadoPorJugarse() {
        Equipo local = new Equipo(1L, "Boca");
        Equipo visitante = new Equipo(2L, "River");
        Fecha fecha = fecha(1L, EstadoFecha.PROGRAMADA);
        PartidoCreateDTO dto = new PartidoCreateDTO(LocalDateTime.now().plusDays(1), 1L, 2L, 1L);

        when(equipoRepository.findById(1L)).thenReturn(Optional.of(local));
        when(equipoRepository.findById(2L)).thenReturn(Optional.of(visitante));
        when(fechaRepository.findById(1L)).thenReturn(Optional.of(fecha));
        when(partidoRepository.save(any(Partido.class))).thenAnswer(invocation -> {
            Partido partido = invocation.getArgument(0);
            partido.setId(10L);
            return partido;
        });

        var response = partidoService.crearPartido(dto);

        assertEquals(10L, response.id());
        assertEquals(EstadoPartido.POR_JUGARSE, response.estado());
        assertEquals(1L, response.equipoLocalId());
        assertEquals(2L, response.equipoVisitanteId());
        verify(partidoRepository).save(any(Partido.class));
    }

    @Test
    void crearPartido_siEquiposSonIguales_deberiaLanzarBadRequest() {
        PartidoCreateDTO dto = new PartidoCreateDTO(LocalDateTime.now().plusDays(1), 1L, 1L, 1L);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> partidoService.crearPartido(dto));

        assertEquals("Los equipos no pueden ser iguales", ex.getMessage());
        verify(partidoRepository, never()).save(any());
    }

    @Test
    void actualizarPartido_siNoEstaPorJugarse_deberiaLanzarBadRequest() {
        Partido partido = partido(10L, EstadoPartido.EN_JUEGO);
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partido));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> partidoService.actualizarPartido(10L,
                        new PartidoUpdateDTO(LocalDateTime.now().plusDays(1), 1L, 2L)));

        assertEquals("Solo se puede modificar un partido con estado POR_JUGARSE", ex.getMessage());
    }

    @Test
    void cambiarEstadoEnJuego_deberiaCambiarEstadoYActualizarFecha() {
        Fecha fecha = fecha(1L, EstadoFecha.PROGRAMADA);
        Partido partido = partido(10L, EstadoPartido.POR_JUGARSE);
        partido.setFecha(fecha);
        fecha.setPartidos(new ArrayList<>(List.of(partido)));

        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partido));
        when(partidoRepository.save(partido)).thenReturn(partido);

        var response = partidoService.cambiarEstadoEnJuego(10L);

        assertEquals(EstadoPartido.EN_JUEGO, response.estado());
        assertEquals(EstadoFecha.EN_JUEGO, fecha.getEstado());
        verify(fechaRepository).save(fecha);
    }

    @Test
    void cargarResultado_deberiaFinalizarPartidoCalcularTendenciaYActualizarPuntos() {
        Fecha fecha = fecha(1L, EstadoFecha.EN_JUEGO);
        Partido partido = partido(10L, EstadoPartido.EN_JUEGO);
        partido.setFecha(fecha);
        fecha.setPartidos(new ArrayList<>(List.of(partido)));

        Usuario exacto = usuario(1L, "Exacto", 0, 0);
        Usuario tendencia = usuario(2L, "Tendencia", 0, 0);
        Usuario errado = usuario(3L, "Errado", 0, 0);

        Pronostico pExacto = pronostico(1L, exacto, partido, 2, 1);
        Pronostico pTendencia = pronostico(2L, tendencia, partido, 3, 1);
        Pronostico pErrado = pronostico(3L, errado, partido, 0, 1);
        partido.setPronosticos(new ArrayList<>(List.of(pExacto, pTendencia, pErrado)));

        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partido));
        when(partidoRepository.save(partido)).thenReturn(partido);

        var response = partidoService.cargarResultado(10L, new PartidoResultadoDTO(2, 1));

        assertEquals(EstadoPartido.FINALIZADO, response.estado());
        assertEquals(ResultadoTendencia.LOCAL, response.resultadoTendencia());
        assertEquals(EstadoFecha.FINALIZADA, fecha.getEstado());

        assertEquals(3, pExacto.getPuntosObtenidos());
        assertEquals(3, exacto.getPuntosTotales());
        assertEquals(1, exacto.getCantidadResultadosExactos());

        assertEquals(1, pTendencia.getPuntosObtenidos());
        assertEquals(1, tendencia.getPuntosTotales());
        assertEquals(0, tendencia.getCantidadResultadosExactos());

        assertEquals(0, pErrado.getPuntosObtenidos());
        assertEquals(0, errado.getPuntosTotales());
    }

    @Test
    void cargarResultado_siPartidoNoEstaEnJuego_deberiaLanzarBadRequest() {
        Partido partido = partido(10L, EstadoPartido.POR_JUGARSE);
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partido));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> partidoService.cargarResultado(10L, new PartidoResultadoDTO(1, 1)));

        assertEquals("Solo se puede cargar resultado en estado EN_JUEGO", ex.getMessage());
    }

    @Test
    void eliminarPartido_deberiaEliminarSiEstaPorJugarseYSinPronosticos() {
        Partido partido = partido(10L, EstadoPartido.POR_JUGARSE);
        partido.setPronosticos(new ArrayList<>());
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partido));

        partidoService.eliminarPartido(10L);

        verify(partidoRepository).delete(partido);
    }

    @Test
    void eliminarPartido_siTienePronosticos_deberiaLanzarBadRequest() {
        Partido partido = partido(10L, EstadoPartido.POR_JUGARSE);
        partido.setPronosticos(new ArrayList<>(List.of(new Pronostico())));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partido));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> partidoService.eliminarPartido(10L));

        assertEquals("No se puede eliminar un partido con pronósticos", ex.getMessage());
        verify(partidoRepository, never()).delete(any());
    }

    @Test
    void buscarPorId_siNoExiste_deberiaLanzarResourceNotFound() {
        when(partidoRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> partidoService.buscarPorId(10L));
    }

    private Partido partido(Long id, EstadoPartido estado) {
        Partido partido = new Partido();
        partido.setId(id);
        partido.setEstado(estado);
        partido.setFechaHorarioInicio(LocalDateTime.now().plusDays(1));
        partido.setEquipoLocal(new Equipo(1L, "Local"));
        partido.setEquipoVisitante(new Equipo(2L, "Visitante"));
        partido.setFecha(fecha(1L, EstadoFecha.PROGRAMADA));
        partido.setPronosticos(new ArrayList<>());
        return partido;
    }

    private Fecha fecha(Long id, EstadoFecha estado) {
        Fecha fecha = new Fecha();
        fecha.setId(id);
        fecha.setNombre("Fecha 1");
        fecha.setEstado(estado);
        fecha.setPartidos(new ArrayList<>());
        return fecha;
    }

    private Usuario usuario(Long id, String nombre, Integer puntos, Integer exactos) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setEmail(nombre.toLowerCase() + "@test.com");
        usuario.setContraseña("123456");
        usuario.setRol(Rol.USER);
        usuario.setPuntosTotales(puntos);
        usuario.setCantidadResultadosExactos(exactos);
        return usuario;
    }

    private Pronostico pronostico(Long id, Usuario usuario, Partido partido, int golesLocal, int golesVisitante) {
        Pronostico pronostico = new Pronostico();
        pronostico.setId(id);
        pronostico.setUsuario(usuario);
        pronostico.setPartido(partido);
        pronostico.setGolesLocalPronosticados(golesLocal);
        pronostico.setGolesVisitantePronosticados(golesVisitante);
        pronostico.setPuntosObtenidos(0);
        return pronostico;
    }
}