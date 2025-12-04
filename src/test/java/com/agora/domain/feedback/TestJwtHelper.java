package com.agora.domain.feedback;

import io.smallrye.jwt.build.Jwt;
import java.time.Duration;
import java.time.Instant;

/**
 * Test utility for generating valid JWT tokens for testing authenticated endpoints.
 */
public class TestJwtHelper {

    private static final String ISSUER = "agora.feedback";
    private static final long TOKEN_EXPIRY_SECONDS = 86400; // 24 hours

    /**
     * Generate a valid JWT token with the issuer claim matching configuration.
     *
     * @param userId The user ID to set as the sub claim
     * @param username The username claim
     * @param email The email claim
     * @return A valid JWT token string
     */
    public static String generateTestToken(String userId, String username, String email) {
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
     * Generate a valid JWT token with custom expiration.
     *
     * @param userId The user ID to set as the sub claim
     * @param username The username claim
     * @param email The email claim
     * @param expiration The token expiration duration
     * @return A valid JWT token string
     */
    public static String generateTestToken(String userId, String username, String email, Duration expiration) {
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
