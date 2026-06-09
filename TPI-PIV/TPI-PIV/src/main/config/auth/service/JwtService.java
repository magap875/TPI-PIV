package com.tpi.piv.config.auth.service;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "clave_super_secreta_para_firmar_tokens_jwt_2026_segura";

    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15;
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generarAccessToken(String email) {
        return generarToken(email, ACCESS_TOKEN_EXPIRATION);
    }

    public String generarRefreshToken(String email) {
        return generarToken(email, REFRESH_TOKEN_EXPIRATION);
    }

    private String generarToken(String email, long expiration) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String obtenerEmailDelToken(String token) {
        return obtenerClaims(token).getSubject();
    }

    public boolean tokenEsValido(String token) {
        return !obtenerClaims(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims obtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}