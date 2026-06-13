package com.example.features.miembrosgrupos.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record UnirseGrupoRequestDTO(

        @NotBlank(message = "El código de invitación es obligatorio")
        String codigoInvitacion

) {
}