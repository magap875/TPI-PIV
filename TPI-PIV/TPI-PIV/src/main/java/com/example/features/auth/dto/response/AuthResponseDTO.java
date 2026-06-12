package com.example.features.auth.dto.response;

public record AuthResponseDTO(
        String accessToken,
        String refreshToken
) {}