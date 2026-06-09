public class PronosticoMapper {

    public static PronosticoResponseDTO toResponseDTO(Pronostico pronostico) {

        return new PronosticoResponseDTO(
                pronostico.getId(),
                pronostico.getUsuario().getId(),
                pronostico.getUsuario().getNombre(),
                pronostico.getPartido().getId(),
                pronostico.getGolesLocalPronosticados(),
                pronostico.getGolesVisitantePronosticados(),
                pronostico.getFechaCreacion(),
                pronostico.getPuntosObtenidos()
        );
    }
}