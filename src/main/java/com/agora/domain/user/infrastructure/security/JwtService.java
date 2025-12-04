package com.agora.domain.user.infrastructure.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.time.Duration;
import java.util.UUID;

/**
 * Service for generating internal JWT tokens
 */
@ApplicationScoped
public class JwtService {

    private static final long TOKEN_EXPIRY_SECONDS = 86400; // 24 hours
    private static final String ISSUER = "agora.feedback";

    /**
     * Generate JWT token for authenticated user
     */
    public String generateToken(String userId, String username, String email) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(TOKEN_EXPIRY_SECONDS);

        return Jwt
                .issuer(ISSUER)
                .subject("user")
                .upn(email)
                .claim("sub", userId)
                .claim("username", username)
                .claim("email", email)
                .issuedAt(now)
                .expiresAt(expiration)
                .sign();
    }

    /**
     * Generate token with custom expiration
     */
    public String generateToken(String userId, String username, String email, Duration expiration) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(expiration);

        return Jwt
                .issuer(ISSUER)
                .subject("user")
                .upn(email)
                .claim("sub", userId)
                .claim("username", username)
                .claim("email", email)
                .issuedAt(now)
                .expiresAt(expirationTime)
                .sign();
    }
}
