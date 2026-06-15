package com.example.features.users.dtos.request;

import jakarta.validation.constraints.*;

public record UsuarioUpdateDTO(
        @Size(min = 2, max = 20, message = "El nombre debe tener entre 2 y 20 caracteres")
        String nombre,

        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String contraseña
){}