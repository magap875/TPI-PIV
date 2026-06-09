public class FechaMapper {

    public static Fecha toEntity(FechaRequestDTO dto) {

        Fecha fecha = new Fecha();

        fecha.setNombre(dto.nombre());
        fecha.setEstado(dto.estado());

        return fecha;
    }

    public static FechaResponseDTO toResponseDTO(Fecha fecha) {

        return new FechaResponseDTO(
                fecha.getId(),
                fecha.getNombre(),
                fecha.getEstado()
        );
    }
}