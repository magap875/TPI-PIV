
package com.example.features.equipos.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EquipoUpdateDTO(
        @NotBlank(message = "El nombre del equipo es obligatorio")
        @Size(min = 2, max = 15, message = "El nombre del equipo debe tener entre 2 y 15 caracteres")
        String nombre
){
}
