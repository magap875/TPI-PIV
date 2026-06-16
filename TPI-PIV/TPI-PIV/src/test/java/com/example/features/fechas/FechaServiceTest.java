package com.example.features.fechas;

import com.example.config.exceptions.BadRequestException;
import com.example.config.exceptions.ResourceNotFoundException;
import com.example.features.fechas.dtos.request.FechaCreateDTO;
import com.example.features.fechas.dtos.request.FechaUpdateDTO;
import com.example.features.fechas.models.EstadoFecha;
import com.example.features.fechas.models.Fecha;
import com.example.features.fechas.repositories.FechaRepository;
import com.example.features.fechas.services.impl.FechaService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FechaServiceTest {

    @Mock
    private FechaRepository fechaRepository;

    @InjectMocks
    private FechaService fechaService;

    @Test
    void crear_siNombreNoExiste_deberiaCrearFechaProgramada() {
        when(fechaRepository.existsByNombre("Fecha 1")).thenReturn(false);
        when(fechaRepository.save(any(Fecha.class))).thenAnswer(invocation -> {
            Fecha fecha = invocation.getArgument(0);
            fecha.setId(1L);
            return fecha;
        });

        var response = fechaService.crear(new FechaCreateDTO("Fecha 1"));

        assertEquals(1L, response.id());
        assertEquals("Fecha 1", response.nombre());
        assertEquals(EstadoFecha.PROGRAMADA, response.estado());
    }

    @Test
    void crear_siNombreExiste_deberiaLanzarBadRequest() {
        when(fechaRepository.existsByNombre("Fecha 1")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> fechaService.crear(new FechaCreateDTO("Fecha 1")));

        assertEquals("Ya existe una fecha con ese nombre", ex.getMessage());
        verify(fechaRepository, never()).save(any());
    }

    @Test
    void actualizar_siExiste_deberiaModificarNombre() {
        Fecha fecha = fecha(1L, "Fecha 1", EstadoFecha.PROGRAMADA);
        when(fechaRepository.findById(1L)).thenReturn(Optional.of(fecha));
        when(fechaRepository.save(fecha)).thenReturn(fecha);

        var response = fechaService.actualizar(1L, new FechaUpdateDTO("Fecha 2"));

        assertEquals("Fecha 2", response.nombre());
    }

    @Test
    void eliminar_siFechaNoExiste_deberiaLanzarResourceNotFound() {
        when(fechaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fechaService.eliminar(99L));
    }

    private Fecha fecha(Long id, String nombre, EstadoFecha estado) {
        Fecha fecha = new Fecha();
        fecha.setId(id);
        fecha.setNombre(nombre);
        fecha.setEstado(estado);
        fecha.setPartidos(new ArrayList<>());
        return fecha;
    }
}
