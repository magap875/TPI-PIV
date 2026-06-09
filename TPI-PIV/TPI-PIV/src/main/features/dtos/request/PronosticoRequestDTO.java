import jakarta.validation.constraints.*;

public record PronosticoRequestDTO(

        @NotNull(message = "Los goles del local son obligatorios")
        @PositiveOrZero(message = "Los goles del local no pueden ser negativos")
        Integer golesLocalPronosticados,

        @NotNull(message = "Los goles del visitante son obligatorios")
        @PositiveOrZero(message = "Los goles del visitante no pueden ser negativos")
        Integer golesVisitantePronosticados,

        @NotNull(message = "El ID del usuario es obligatorio")
        Long usuarioId,

        @NotNull(message = "El ID del partido es obligatorio")
        Long partidoId
) {
}