package com.example.features.grupos;

import com.example.features.grupos.dtos.request.GrupoRequestDTO;
import com.example.features.grupos.models.Grupo;
import com.example.features.grupos.repositories.GrupoRepository;
import com.example.features.grupos.services.impl.GrupoServiceImpl;
import com.example.features.miembrosgrupos.dtos.request.UnirseGrupoRequestDTO;
import com.example.features.miembrosgrupos.models.MiembroGrupo;
import com.example.features.miembrosgrupos.repositories.MiembroGrupoRepository;
import com.example.features.pronosticos.repositories.PronosticoRepository;
import com.example.features.users.models.Rol;
import com.example.features.users.models.Usuario;
import com.example.features.users.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrupoServiceImplTest {

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MiembroGrupoRepository miembroGrupoRepository;
    
    @Mock
    private PronosticoRepository pronosticoRepository;

    @InjectMocks
    private GrupoServiceImpl grupoService;

    @Test
    void crearGrupo_deberiaCrearGrupoYAgregarAlCreadorComoMiembro() {
        Usuario usuario = usuario(1L, "Mariano", "m@test.com", 0, 0);
        GrupoRequestDTO dto = new GrupoRequestDTO("Los Pibes");

        when(usuarioRepository.findByEmail("m@test.com")).thenReturn(Optional.of(usuario));
        when(grupoRepository.existsByCodigoInvitacion(anyString())).thenReturn(false);
        when(grupoRepository.save(any(Grupo.class))).thenAnswer(invocation -> {
            Grupo grupo = invocation.getArgument(0);
            grupo.setId(10L);
            return grupo;
        });

        var response = grupoService.crearGrupo(dto, "m@test.com");

        assertEquals(10L, response.id());
        assertEquals("Los Pibes", response.nombre());
        assertNotNull(response.codigoInvitacion());
        assertEquals(8, response.codigoInvitacion().length());

        ArgumentCaptor<MiembroGrupo> miembroCaptor = ArgumentCaptor.forClass(MiembroGrupo.class);
        verify(miembroGrupoRepository).save(miembroCaptor.capture());
        MiembroGrupo miembroGuardado = miembroCaptor.getValue();
        assertEquals(usuario, miembroGuardado.getUsuario());
        assertEquals(10L, miembroGuardado.getGrupo().getId());
        assertNotNull(miembroGuardado.getFechaIngreso());
    }

    @Test
    void crearGrupo_siUsuarioNoExiste_deberiaLanzarExcepcion() {
        when(usuarioRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> grupoService.crearGrupo(new GrupoRequestDTO("Grupo"), "noexiste@test.com"));

        assertEquals("Usuario no encontrado", ex.getMessage());
        verify(grupoRepository, never()).save(any());
        verify(miembroGrupoRepository, never()).save(any());
    }

    @Test
    void unirseAGrupo_deberiaUnirUsuarioAlGrupoConCodigo() {
        Usuario usuario = usuario(1L, "Mariano", "m@test.com", 0, 0);
        Grupo grupo = grupo(2L, "Grupo", "ABC12345");

        when(usuarioRepository.findByEmail("m@test.com")).thenReturn(Optional.of(usuario));
        when(grupoRepository.findByCodigoInvitacion("ABC12345")).thenReturn(Optional.of(grupo));
        when(miembroGrupoRepository.existsByUsuarioIdAndGrupoId(1L, 2L)).thenReturn(false);
        when(miembroGrupoRepository.save(any(MiembroGrupo.class))).thenAnswer(invocation -> {
            MiembroGrupo miembro = invocation.getArgument(0);
            miembro.setId(99L);
            return miembro;
        });

        var response = grupoService.unirseAGrupo(new UnirseGrupoRequestDTO("ABC12345"), "m@test.com");

        assertEquals(99L, response.id());
        assertEquals(1L, response.usuarioId());
        assertEquals(2L, response.grupoId());
        verify(miembroGrupoRepository).save(any(MiembroGrupo.class));
    }

    @Test
    void unirseAGrupo_siCodigoNoExiste_deberiaLanzarExcepcion() {
        Usuario usuario = usuario(1L, "Mariano", "m@test.com", 0, 0);
        when(usuarioRepository.findByEmail("m@test.com")).thenReturn(Optional.of(usuario));
        when(grupoRepository.findByCodigoInvitacion("MALO")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> grupoService.unirseAGrupo(new UnirseGrupoRequestDTO("MALO"), "m@test.com"));

        assertEquals("Código de invitación inválido", ex.getMessage());
        verify(miembroGrupoRepository, never()).save(any());
    }

    @Test
    void unirseAGrupo_siYaEsMiembro_deberiaLanzarExcepcion() {
        Usuario usuario = usuario(1L, "Mariano", "m@test.com", 0, 0);
        Grupo grupo = grupo(2L, "Grupo", "ABC12345");

        when(usuarioRepository.findByEmail("m@test.com")).thenReturn(Optional.of(usuario));
        when(grupoRepository.findByCodigoInvitacion("ABC12345")).thenReturn(Optional.of(grupo));
        when(miembroGrupoRepository.existsByUsuarioIdAndGrupoId(1L, 2L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> grupoService.unirseAGrupo(new UnirseGrupoRequestDTO("ABC12345"), "m@test.com"));

        assertEquals("El usuario ya pertenece a este grupo", ex.getMessage());
        verify(miembroGrupoRepository, never()).save(any());
    }

    @Test
    void obtenerRankingGrupo_deberiaOrdenarPorPuntosYResultadosExactos() {
        Grupo grupo = grupo(1L, "Grupo", "ABC12345");
        Usuario usuarioA = usuario(1L, "A", "a@test.com", 10, 1);
        Usuario usuarioB = usuario(2L, "B", "b@test.com", 15, 0);
        Usuario usuarioC = usuario(3L, "C", "c@test.com", 10, 3);

        when(grupoRepository.existsById(1L)).thenReturn(true);
        when(pronosticoRepository.findAll()).thenReturn(List.of());
        when(miembroGrupoRepository.findByGrupoId(1L)).thenReturn(List.of(
                miembro(1L, usuarioA, grupo),
                miembro(2L, usuarioB, grupo),
                miembro(3L, usuarioC, grupo)));

        var ranking = grupoService.obtenerRankingGrupo(1L);

        assertEquals(List.of("B", "C", "A"), ranking.stream().map(r -> r.nombre()).toList());
    }

    @Test
    void salirDelGrupo_deberiaEliminarMiembro() {
        Usuario usuario = usuario(1L, "Mariano", "m@test.com", 0, 0);
        Grupo grupo = grupo(2L, "Grupo", "ABC12345");
        MiembroGrupo miembro = miembro(10L, usuario, grupo);

        when(usuarioRepository.findByEmail("m@test.com")).thenReturn(Optional.of(usuario));
        when(miembroGrupoRepository.findByUsuarioIdAndGrupoId(1L, 2L)).thenReturn(Optional.of(miembro));

        grupoService.salirDelGrupo(2L, "m@test.com");

        verify(miembroGrupoRepository).delete(miembro);
    }

    @Test
    void salirDelGrupo_siNoPertenece_deberiaLanzarExcepcion() {
        Usuario usuario = usuario(1L, "Mariano", "m@test.com", 0, 0);
        when(usuarioRepository.findByEmail("m@test.com")).thenReturn(Optional.of(usuario));
        when(miembroGrupoRepository.findByUsuarioIdAndGrupoId(1L, 2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> grupoService.salirDelGrupo(2L, "m@test.com"));

        assertEquals("El usuario no pertenece a este grupo", ex.getMessage());
        verify(miembroGrupoRepository, never()).delete(any());
    }

    private Usuario usuario(Long id, String nombre, String email, Integer puntos, Integer exactos) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setContraseña("123456");
        usuario.setRol(Rol.USER);
        usuario.setPuntosTotales(puntos);
        usuario.setCantidadResultadosExactos(exactos);
        return usuario;
    }

    private Grupo grupo(Long id, String nombre, String codigo) {
        Grupo grupo = new Grupo();
        grupo.setId(id);
        grupo.setNombre(nombre);
        grupo.setCodigoInvitacion(codigo);
        return grupo;
    }

    private MiembroGrupo miembro(Long id, Usuario usuario, Grupo grupo) {
        MiembroGrupo miembro = new MiembroGrupo();
        miembro.setId(id);
        miembro.setUsuario(usuario);
        miembro.setGrupo(grupo);
        return miembro;
    }
}
