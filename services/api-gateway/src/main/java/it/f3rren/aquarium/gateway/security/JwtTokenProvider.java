package it.f3rren.aquarium.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.f3rren.aquarium.gateway.config.JwtProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

/**
 * Issues and validates the gateway's own JWTs (HS256). The raw
 * {@code jwt.secret} is hashed
 * with SHA-256 before use as the signing key, rather than used directly: HS256
 * requires a key
 * of at least 256 bits, and hashing guarantees that length regardless of how
 * long a value an
 * operator actually sets for the secret, avoiding a confusing
 * {@code WeakKeyException} at
 * startup for a shorter (but still reasonable) dev secret.
 */
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMinutes;

    public JwtTokenProvider(JwtProperties properties) {
        this.key = deriveKey(properties.getSecret());
        this.expirationMinutes = properties.getExpirationMinutes();
    }

    public String issueToken(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    public Optional<String> validateAndGetSubject(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return Optional.ofNullable(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static SecretKey deriveKey(String secret) {
        try {
            byte[] hashed = MessageDigest.getInstance("SHA-256").digest(secret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
