package com.example.features.fechas.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FechaUpdateDTO(
        @NotBlank(message = "El nombre es obligatorio.")
        @Size(min = 3, max = 100, message = "El nombre de la fecha debe tener entre 3 y 100 caracteres")
        String nombre
){}
