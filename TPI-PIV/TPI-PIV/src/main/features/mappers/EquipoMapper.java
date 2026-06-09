public class EquipoMapper {

    public static Equipo toEntity(EquipoRequestDTO dto) {

        Equipo equipo = new Equipo();

        equipo.setNombre(dto.nombre());

        return equipo;
    }

    public static EquipoResponseDTO toResponseDTO(Equipo equipo) {

        return new EquipoResponseDTO(
                equipo.getId(),
                equipo.getNombre()
        );
    }
}
