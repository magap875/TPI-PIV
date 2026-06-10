public record MiembroGrupoResponseDTO(

        Long id,

        Long usuarioId,
        String usuarioNombre,

        Long grupoId,
        String grupoNombre,

        LocalDateTime fechaIngreso

) {
}