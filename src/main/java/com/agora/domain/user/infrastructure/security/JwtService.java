package com.agora.domain.user.infrastructure.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.time.Duration;
import java.util.UUID;

/**
 * Service for generating internal JWT tokens
 */
@ApplicationScoped
public class JwtService {

    /**
     * Generate JWT token for authenticated user
     */
    public String generateToken(String userId, String username, String email) {
        return Jwt
                .subject(userId)
                .claim("username", username)
                .claim("email", email)
                .claim("preferred_username", username)  // Standard JWT claim
                .groups("user")  // Basic role
                //.expiresIn(Duration.ofSeconds(durationSeconds))
                .sign();
    }

    /**
     * Generate token with custom expiration
     */
    public String generateToken(UUID userId, String username, String email, Duration expiration) {
        return Jwt
                .subject(userId.toString())
                .claim("username", username)
                .claim("email", email)
                .claim("preferred_username", username)
                .groups("user")
                .expiresIn(expiration)
                .sign();
    }
}
