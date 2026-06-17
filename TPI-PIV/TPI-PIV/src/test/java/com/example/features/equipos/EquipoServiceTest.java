package com.example.features.equipos;

import com.example.config.exceptions.BadRequestException;
import com.example.config.exceptions.ResourceNotFoundException;
import com.example.features.equipos.dtos.request.EquipoCreateDTO;
import com.example.features.equipos.dtos.request.EquipoUpdateDTO;
import com.example.features.equipos.models.Equipo;
import com.example.features.equipos.repositories.EquipoRepository;
import com.example.features.equipos.services.impl.EquipoService;
import com.example.features.partidos.repositories.PartidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipoServiceTest {

    @Mock
    private EquipoRepository equipoRepository;

    @Mock
    private PartidoRepository partidoRepository;

    @InjectMocks
    private EquipoService equipoService;

    @Test
    void crear_siNombreNoExiste_deberiaCrearEquipo() {
        when(equipoRepository.existsByNombreIgnoreCase("Boca")).thenReturn(false);
        when(equipoRepository.save(any(Equipo.class))).thenAnswer(invocation -> {
            Equipo equipo = invocation.getArgument(0);
            equipo.setId(1L);
            return equipo;
        });

        var response = equipoService.crear(new EquipoCreateDTO("Boca"));

        assertEquals(1L, response.id());
        assertEquals("Boca", response.nombre());
    }

    @Test
    void crear_siNombreExiste_deberiaLanzarBadRequest() {
        when(equipoRepository.existsByNombreIgnoreCase("Boca")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> equipoService.crear(new EquipoCreateDTO("Boca")));

        assertEquals("Ya existe un equipo con ese nombre.", ex.getMessage());
        verify(equipoRepository, never()).save(any());
    }

    @Test
    void eliminar_siTienePartidosAsociados_deberiaLanzarBadRequest() {
        Equipo equipo = new Equipo(1L, "Boca");
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipo));
        when(partidoRepository.existsByEquipoLocalIdOrEquipoVisitanteId(1L, 1L)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> equipoService.eliminar(1L));

        assertEquals("No se puede eliminar un equipo que tiene partidos asociados.", ex.getMessage());
        verify(equipoRepository, never()).delete(any());
    }

    @Test
    void eliminar_siNoTienePartidos_deberiaEliminar() {
        Equipo equipo = new Equipo(1L, "Boca");
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipo));
        when(partidoRepository.existsByEquipoLocalIdOrEquipoVisitanteId(1L, 1L)).thenReturn(false);

        equipoService.eliminar(1L);

        verify(equipoRepository).delete(equipo);
    }

    @Test
    void actualizarEquipo_siExiste_deberiaActualizarNombre() {
        Equipo equipo = new Equipo(1L, "Boca");
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipo));
        when(equipoRepository.save(equipo)).thenReturn(equipo);

        var response = equipoService.actualizarEquipo(1L, new EquipoUpdateDTO("Boca Juniors"));

        assertEquals("Boca Juniors", response.nombre());
    }

    @Test
    void buscarPorId_siNoExiste_deberiaLanzarResourceNotFound() {
        when(equipoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> equipoService.buscarPorId(99L));
    }
}
