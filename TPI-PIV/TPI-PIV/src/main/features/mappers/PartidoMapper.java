public class PartidoMapper {

    public static PartidoResponseDTO toResponseDTO(Partido partido) {

        return new PartidoResponseDTO(
                partido.getId(),
                partido.getFechaHorarioInicio(),
                partido.getEstado(),
                partido.getEquipoLocal().getId(),
                partido.getEquipoLocal().getNombre(),
                partido.getEquipoVisitante().getId(),
                partido.getEquipoVisitante().getNombre(),
                partido.getGolesLocal(),
                partido.getGolesVisitante(),
                partido.getResultadoTendencia(),
                partido.getFecha().getId(),
                partido.getFecha().getNombre()
        );
    }
}
