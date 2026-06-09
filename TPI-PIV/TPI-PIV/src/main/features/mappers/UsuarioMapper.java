public class UsuarioMapper {

    public static Usuario toEntity(UsuarioRequestDTO dto) {

        Usuario usuario = new Usuario();

        usuario.setNombre(dto.nombre());
        usuario.setEmail(dto.email());
        usuario.setContrasena(dto.contrasena());
        usuario.setRol(dto.rol());

        return usuario;
    }

    public static UsuarioResponseDTO toResponseDTO(Usuario usuario) {

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getPuntosTotales(),
                usuario.getCantidadResultadosExactos()
        );
    }
}