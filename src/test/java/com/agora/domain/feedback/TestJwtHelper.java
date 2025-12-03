package com.agora.domain.feedback;

import io.smallrye.jwt.build.Jwt;
import java.time.Duration;

/**
 * Test utility for generating valid JWT tokens for testing authenticated endpoints.
 */
public class TestJwtHelper {

    /**
     * Generate a valid JWT token with the issuer claim matching configuration.
     *
     * @param userId The user ID to set as the token subject
     * @param username The username claim
     * @param email The email claim
     * @return A valid JWT token string
     */
    public static String generateTestToken(String userId, String username, String email) {
        return Jwt
                .issuer("agora.feedback")
                .subject(userId)
                .claim("username", username)
                .claim("email", email)
                .claim("preferred_username", username)
                .groups("user")
                .sign();
    }

    /**
     * Generate a valid JWT token with custom expiration.
     *
     * @param userId The user ID to set as the token subject
     * @param username The username claim
     * @param email The email claim
     * @param expiration The token expiration duration
     * @return A valid JWT token string
     */
    public static String generateTestToken(String userId, String username, String email, Duration expiration) {
        return Jwt
                .issuer("agora.feedback")
                .subject(userId)
                .claim("username", username)
                .claim("email", email)
                .claim("preferred_username", username)
                .groups("user")
                .expiresIn(expiration)
                .sign();
    }
}
