import java.time.LocalDateTime;

public record PartidoResponseDTO(

        Long id,

        LocalDateTime fechaHorarioInicio,

        EstadoPartido estado,

        Long equipoLocalId,
        String equipoLocal,

        Long equipoVisitanteId,
        String equipoVisitante,

        Integer golesLocal,
        Integer golesVisitante,

        ResultadoTendencia resultadoTendencia,

        Long fechaId,
        String fechaNombre

) {
}