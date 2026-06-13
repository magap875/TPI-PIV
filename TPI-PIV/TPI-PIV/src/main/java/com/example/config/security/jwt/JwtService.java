package com.example.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    // ---------------- KEY ----------------

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(properties.secret().getBytes());
    }

    // ---------------- ACCESS TOKEN ----------------

    public String generarToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + properties.accessExpirationMs()))
                .signWith(getSigningKey())
                .compact();
    }

    // ---------------- REFRESH TOKEN ----------------

    public String generarRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + properties.refreshExpirationMs()))
                .signWith(getSigningKey())
                .compact();
    }

    // ---------------- EXTRAER ----------------

    public String extraerEmail(String token) {
        return obtenerClaims(token).getSubject();
    }

    // ---------------- VALIDAR ----------------

    public boolean esValido(String token) {
        try {
            return obtenerClaims(token)
                    .getExpiration()
                    .after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // ---------------- PARSER MODERNO ----------------

    private Claims obtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}