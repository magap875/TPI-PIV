import jakarta.validation.constraints.*;

public record UsuarioRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 20, message = "El nombre debe tener entre 2 y 20 caracteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 15, message = "La contraseña debe tener al menos 6 caracteres")
        String contraseña
) {
}
