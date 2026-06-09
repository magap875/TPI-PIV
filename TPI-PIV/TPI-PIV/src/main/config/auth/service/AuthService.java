import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponseDTO register(AuthRequestDTO dto) {
        Usuario usuario = new Usuario();

        usuario.setNombre(dto.nombre());
        usuario.setEmail(dto.email());
        usuario.setContrasena(passwordEncoder.encode(dto.contrasena()));
        usuario.setRol(Rol.USER);
        usuario.setPuntosTotales(0);
        usuario.setCantidadResultadosExactos(0);

        Usuario guardado = usuarioRepository.save(usuario);

        String accessToken = jwtService.generarAccessToken(guardado.getEmail());
        String refreshToken = jwtService.generarRefreshToken(guardado.getEmail());

        return new AuthResponseDTO(
                accessToken,
                refreshToken,
                guardado.getId(),
                guardado.getNombre(),
                guardado.getEmail(),
                guardado.getRol());
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.email(),
                        dto.contrasena()));

        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String accessToken = jwtService.generarAccessToken(usuario.getEmail());
        String refreshToken = jwtService.generarRefreshToken(usuario.getEmail());

        return new AuthResponseDTO(
                accessToken,
                refreshToken,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol());
    }

    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO dto) {

        String email = jwtService.obtenerEmailDelToken(dto.refreshToken());

        if (!jwtService.tokenEsValido(dto.refreshToken())) {
            throw new RuntimeException("Refresh token inválido o expirado");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String nuevoAccessToken = jwtService.generarAccessToken(usuario.getEmail());
        String nuevoRefreshToken = jwtService.generarRefreshToken(usuario.getEmail());

        return new AuthResponseDTO(
                nuevoAccessToken,
                nuevoRefreshToken,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol());
    }

}