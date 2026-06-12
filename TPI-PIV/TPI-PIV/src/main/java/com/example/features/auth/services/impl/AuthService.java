package com.example.features.auth.services.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.config.jwt.JwtService;
import com.example.features.auth.dto.request.LoginRequestDTO;
import com.example.features.auth.dto.response.AuthResponseDTO;
import com.example.features.auth.services.interfaces.IAuthService;
import com.example.features.users.dtos.request.UsuarioRegisterDTO;
import com.example.features.users.models.Rol;
import com.example.features.users.models.Usuario;
import com.example.features.users.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService{

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(UsuarioRegisterDTO dto) {

        if (repo.existsByEmail(dto.email())) {
            throw new RuntimeException("Email ya registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.nombre());
        usuario.setEmail(dto.email());
        usuario.setContraseña(encoder.encode(dto.contraseña()));
        usuario.setRol(Rol.USER);

        repo.save(usuario);
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
    try {

        Authentication authentication =
                authenticationManager.authenticate(
                        UsernamePasswordAuthenticationToken.unauthenticated(
                                dto.email(),
                                dto.contraseña()
                        )
                );

        String email = authentication.getName();

        String accessToken = jwtService.generarToken(email);
        String refreshToken = jwtService.generarRefreshToken(email);

        return new AuthResponseDTO(accessToken, refreshToken);

    } catch (BadCredentialsException e) {
        throw new RuntimeException("Credenciales inválidas");
        }
    }

    @Override
    public AuthResponseDTO refresh(String refreshToken) {

        if (!jwtService.esValido(refreshToken)) {
            throw new RuntimeException("Refresh token inválido o expirado");
        }

        String email = jwtService.extraerEmail(refreshToken);

        Usuario usuario = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String newAccessToken = jwtService.generarToken(usuario.getEmail());
        String newRefreshToken = jwtService.generarRefreshToken(usuario.getEmail());

        return new AuthResponseDTO(newAccessToken, newRefreshToken);
    }
}