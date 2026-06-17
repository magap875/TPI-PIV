package com.example.features.auth;

import com.example.config.security.jwt.JwtService;
import com.example.features.auth.dto.request.LoginRequestDTO;
import com.example.features.auth.services.impl.AuthService;
import com.example.features.users.dtos.request.UsuarioRegisterDTO;
import com.example.features.users.models.Rol;
import com.example.features.users.models.Usuario;
import com.example.features.users.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_deberiaGuardarUsuarioConPasswordHasheadaYRolUser() {
        UsuarioRegisterDTO dto = new UsuarioRegisterDTO("Mariano", "m@test.com", "123456");
        when(usuarioRepository.existsByEmail("m@test.com")).thenReturn(false);
        when(encoder.encode("123456")).thenReturn("HASH");

        authService.register(dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario guardado = captor.getValue();

        assertEquals("Mariano", guardado.getNombre());
        assertEquals("m@test.com", guardado.getEmail());
        assertEquals("HASH", guardado.getContraseña());
        assertEquals(Rol.USER, guardado.getRol());
    }

    @Test
    void register_siEmailYaExiste_deberiaLanzarExcepcion() {
        when(usuarioRepository.existsByEmail("m@test.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.register(new UsuarioRegisterDTO("Mariano", "m@test.com", "123456")));

        assertEquals("Email ya registrado", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void login_conCredencialesValidas_deberiaDevolverAccessYRefreshToken() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getName()).thenReturn("m@test.com");
        when(jwtService.generarToken("m@test.com")).thenReturn("ACCESS");
        when(jwtService.generarRefreshToken("m@test.com")).thenReturn("REFRESH");

        var response = authService.login(new LoginRequestDTO("m@test.com", "123456"));

        assertEquals("ACCESS", response.accessToken());
        assertEquals("REFRESH", response.refreshToken());
    }

    @Test
    void login_conCredencialesInvalidas_deberiaLanzarExcepcion() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(new LoginRequestDTO("m@test.com", "mal")));

        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    @Test
    void refresh_conRefreshValido_deberiaGenerarTokensNuevos() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("m@test.com");

        when(jwtService.esValido("REFRESH_OLD")).thenReturn(true);
        when(jwtService.extraerEmail("REFRESH_OLD")).thenReturn("m@test.com");
        when(usuarioRepository.findByEmail("m@test.com")).thenReturn(Optional.of(usuario));
        when(jwtService.generarToken("m@test.com")).thenReturn("ACCESS_NEW");
        when(jwtService.generarRefreshToken("m@test.com")).thenReturn("REFRESH_NEW");

        var response = authService.refresh("REFRESH_OLD");

        assertEquals("ACCESS_NEW", response.accessToken());
        assertEquals("REFRESH_NEW", response.refreshToken());
    }

    @Test
    void refresh_conRefreshInvalido_deberiaLanzarExcepcion() {
        when(jwtService.esValido("INVALIDO")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.refresh("INVALIDO"));

        assertEquals("Refresh token inválido o expirado", ex.getMessage());
        verify(usuarioRepository, never()).findByEmail(anyString());
    }
}
