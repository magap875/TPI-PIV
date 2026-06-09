import jakarta.validation.constraints.*;

public record GrupoRequestDTO(

        @NotBlank(message = "El nombre del grupo es obligatorio")
        @Size(min = 3, max = 20, message = "El nombre del grupo debe tener entre 3 y 20 caracteres")
        String nombre,

        @NotBlank(message = "El código de invitación es obligatorio")
        @Size(min = 4, max = 20, message = "El código de invitación debe tener entre 4 y 20 caracteres")
        String codigoInvitacion
) {
}