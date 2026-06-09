public record AuthResponseDTO(

        String accessToken,
        String refreshToken,
        Long usuarioId,
        String nombre,
        String email,
        Rol rol

) {
}
