public class GrupoMapper {

    public static Grupo toEntity(GrupoRequestDTO dto) {

        Grupo grupo = new Grupo();

        grupo.setNombre(dto.nombre());
        grupo.setCodigoInvitacion(dto.codigoInvitacion());

        return grupo;
    }

    public static GrupoResponseDTO toResponseDTO(Grupo grupo) {

        return new GrupoResponseDTO(
                grupo.getId(),
                grupo.getNombre(),
                grupo.getCodigoInvitacion()
        );
    }
}
