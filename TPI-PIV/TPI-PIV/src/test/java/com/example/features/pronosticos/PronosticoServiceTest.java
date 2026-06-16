package com.example.features.pronosticos;

import com.example.config.exceptions.BadRequestException;
import com.example.config.exceptions.ResourceNotFoundException;
import com.example.features.equipos.models.Equipo;
import com.example.features.fechas.models.EstadoFecha;
import com.example.features.fechas.models.Fecha;
import com.example.features.partidos.models.EstadoPartido;
import com.example.features.partidos.models.Partido;
import com.example.features.partidos.repositories.PartidoRepository;
import com.example.features.pronosticos.dtos.request.PronosticoRequestDTO;
import com.example.features.pronosticos.models.Pronostico;
import com.example.features.pronosticos.models.ResultadoTendencia;
import com.example.features.pronosticos.repositories.PronosticoRepository;
import com.example.features.pronosticos.services.impl.PronosticoService;
import com.example.features.users.models.Rol;
import com.example.features.users.models.Usuario;
import com.example.features.users.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PronosticoServiceTest {

    @Mock
    private PronosticoRepository pronosticoRepository;

    @Mock
    private PartidoRepository partidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PronosticoService pronosticoService;

    @Test
    void crearOActualizar_siNoExistePronostico_deberiaCrearUnoNuevo() {
        Usuario usuario = usuario(1L, "Mariano", "m@test.com");
        Partido partido = partido(10L, EstadoPartido.POR_JUGARSE, LocalDateTime.now().plusHours(2));
        PronosticoRequestDTO dto = new PronosticoRequestDTO(2, 1);

        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partido));
        when(usuarioRepository.findByEmail("m@test.com")).thenReturn(Optional.of(usuario));
        when(pronosticoRepository.findByUsuarioIdAndPartidoId(1L, 10L)).thenReturn(Optional.empty());
        when(pronosticoRepository.save(any(Pronostico.class))).thenAnswer(invocation -> {
            Pronostico p = invocation.getArgument(0);
            p.setId(100L);
            return p;
        });

        var response = pronosticoService.crearOActualizar("m@test.com", 10L, dto);

        assertEquals(100L, response.id());
        assertEquals(1L, response.usuarioId());
        assertEquals(10L, response.partidoId());
        assertEquals(2, response.golesLocalPronosticados());
        assertEquals(1, response.golesVisitantePronosticados());
        assertEquals(ResultadoTendencia.LOCAL, response.resultadoTendencia());
        assertEquals(0, response.puntosObtenidos());
        assertNotNull(response.fechaCreacion());
    }

    @Test
    void crearOActualizar_siExistePronostico_deberiaActualizarSinCambiarFechaCreacion() {
        Usuario usuario = usuario(1L, "Mariano", "m@test.com");
        Partido partido = partido(10L, EstadoPartido.POR_JUGARSE, LocalDateTime.now().plusHours(2));
        LocalDateTime fechaOriginal = LocalDateTime.now().minusDays(1);
        Pronostico existente = new Pronostico();
        existente.setId(100L);
        existente.setUsuario(usuario);
        existente.setPartido(partido);
        existente.setFechaCreacion(fechaOriginal);
        existente.setPuntosObtenidos(3);

        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partido));
        when(usuarioRepository.findByEmail("m@test.com")).thenReturn(Optional.of(usuario));
        when(pronosticoRepository.findByUsuarioIdAndPartidoId(1L, 10L)).thenReturn(Optional.of(existente));
        when(pronosticoRepository.save(any(Pronostico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = pronosticoService.crearOActualizar("m@test.com", 10L, new PronosticoRequestDTO(1, 3));

        assertEquals(100L, response.id());
        assertEquals(1, response.golesLocalPronosticados());
        assertEquals(3, response.golesVisitantePronosticados());
        assertEquals(ResultadoTendencia.VISITANTE, response.resultadoTendencia());
        assertEquals(0, response.puntosObtenidos());
        assertEquals(fechaOriginal, response.fechaCreacion());
    }

    @Test
    void crearOActualizar_siPartidoNoExiste_deberiaLanzarResourceNotFound() {
        when(partidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> pronosticoService.crearOActualizar("m@test.com", 99L, new PronosticoRequestDTO(1, 1)));
    }

    @Test
    void crearOActualizar_siPartidoNoEstaPorJugarse_deberiaLanzarBadRequest() {
        Partido partido = partido(10L, EstadoPartido.EN_JUEGO, LocalDateTime.now().plusHours(2));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partido));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pronosticoService.crearOActualizar("m@test.com", 10L, new PronosticoRequestDTO(1, 1)));

        assertEquals("No se puede pronosticar este partido debido a su estado actual", ex.getMessage());
    }

    @Test
    void crearOActualizar_siFaltanMenosDe30Minutos_deberiaBloquearPronostico() {
        Partido partido = partido(10L, EstadoPartido.POR_JUGARSE, LocalDateTime.now().plusMinutes(20));
        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partido));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pronosticoService.crearOActualizar("m@test.com", 10L, new PronosticoRequestDTO(1, 1)));

        assertEquals("Ya expiró el tiempo para pronosticar", ex.getMessage());
        verify(usuarioRepository, never()).findByEmail(anyString());
        verify(pronosticoRepository, never()).save(any());
    }

    @Test
    void crearOActualizar_deberiaCalcularTendenciaEmpate() {
        Usuario usuario = usuario(1L, "Mariano", "m@test.com");
        Partido partido = partido(10L, EstadoPartido.POR_JUGARSE, LocalDateTime.now().plusHours(2));

        when(partidoRepository.findById(10L)).thenReturn(Optional.of(partido));
        when(usuarioRepository.findByEmail("m@test.com")).thenReturn(Optional.of(usuario));
        when(pronosticoRepository.findByUsuarioIdAndPartidoId(1L, 10L)).thenReturn(Optional.empty());
        when(pronosticoRepository.save(any(Pronostico.class))).thenAnswer(invocation -> {
            Pronostico p = invocation.getArgument(0);
            p.setId(100L);
            return p;
        });

        var response = pronosticoService.crearOActualizar("m@test.com", 10L, new PronosticoRequestDTO(2, 2));

        assertEquals(ResultadoTendencia.EMPATE, response.resultadoTendencia());
    }

    @Test
    void listarPorUsuarioEmail_siUsuarioNoExiste_deberiaLanzarResourceNotFound() {
        when(usuarioRepository.findByEmail("no@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> pronosticoService.listarPorUsuarioEmail("no@test.com"));
    }

    private Usuario usuario(Long id, String nombre, String email) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setContraseña("123456");
        usuario.setRol(Rol.USER);
        usuario.setPuntosTotales(0);
        usuario.setCantidadResultadosExactos(0);
        return usuario;
    }

    private Partido partido(Long id, EstadoPartido estado, LocalDateTime inicio) {
        Equipo local = new Equipo(1L, "Local");
        Equipo visitante = new Equipo(2L, "Visitante");
        Fecha fecha = new Fecha();
        fecha.setId(1L);
        fecha.setNombre("Fecha 1");
        fecha.setEstado(EstadoFecha.PROGRAMADA);

        Partido partido = new Partido();
        partido.setId(id);
        partido.setEstado(estado);
        partido.setFechaHorarioInicio(inicio);
        partido.setEquipoLocal(local);
        partido.setEquipoVisitante(visitante);
        partido.setFecha(fecha);
        return partido;
    }
}
