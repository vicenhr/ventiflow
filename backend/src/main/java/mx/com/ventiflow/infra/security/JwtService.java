package mx.com.ventiflow.infra.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import mx.com.ventiflow.domain.usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private static final Duration DURACION_TOKEN = Duration.ofHours(8);
    private final SecretKey llave;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.llave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(usuario.getNombre())
                .claim("rol", usuario.getRol().name())
                .expiration(Date.from(ahora.plus(DURACION_TOKEN)))
                .issuedAt(Date.from(ahora))
                .signWith(llave)
                .compact();
    }

    public Claims leerClaims(String token) {
        return Jwts.parser()
                .verifyWith(llave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String obtenerNombre(String token) {
        return leerClaims(token).getSubject();
    }

    public String obtenerRol(String token) {
        return leerClaims(token).get("rol", String.class);
    }
}