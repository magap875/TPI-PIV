package com.example.features.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDTO(

        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken

) {
}