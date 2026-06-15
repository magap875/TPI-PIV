package com.example.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
@AllArgsConstructor

public class JwtService {
    private final JwtProperties properties;

    // key de firma para generar y validar tokens
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(properties.secret().getBytes());
    }

    // generacion del access token
    public String generarToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + properties.accessExpirationMs()))
                .signWith(getSigningKey())
                .compact();
    }

    // generacion del refresh token
    public String generarRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + properties.refreshExpirationMs()))
                .signWith(getSigningKey())
                .compact();
    }

    // extraer email del token
    public String extraerEmail(String token) {
        return obtenerClaims(token).getSubject();
    }

    // validacion de token (firma y expiracion)
    public boolean esValido(String token) {
        try {
            return obtenerClaims(token)
                    .getExpiration()
                    .after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // parser
    private Claims obtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}