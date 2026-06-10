import java.time.LocalDateTime;

public record PronosticoResponseDTO(

        Long id,

        Long usuarioId,
        String usuarioNombre,

        Long partidoId,

        Integer golesLocalPronosticados,
        Integer golesVisitantePronosticados,

        LocalDateTime fechaCreacion,

        Integer puntosObtenidos

) {
}
