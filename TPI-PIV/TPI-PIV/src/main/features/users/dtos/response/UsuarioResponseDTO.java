public record UsuarioResponseDTO(

        Long id,
        String nombre,
        String email,
        Rol rol,
        Integer puntosTotales,
        Integer cantidadResultadosExactos

) {
}
