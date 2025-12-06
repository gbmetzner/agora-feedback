package com.agora.domain.user.infrastructure.security;

import com.agora.domain.feedback.common.IdHelper;
import com.agora.domain.user.model.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Service for generating internal JWT tokens
 */
@ApplicationScoped
public class JwtService {

    private static final long TOKEN_EXPIRY_SECONDS = 86400; // 24 hours
    private static final String ISSUER = "agora.feedback";
    private static final String SUBJECT = "user";

    /**
     * Generate JWT token for authenticated user
     */
    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(TOKEN_EXPIRY_SECONDS);

        Set<String> roles = new HashSet<>();
        roles.add(user.getRole().name());

        return Jwt.issuer(ISSUER)
                .upn(user.getEmail())
                .groups(user.getRole().name())
                .subject(SUBJECT)
                .claim("sub", IdHelper.toString(user.getId()))
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .issuedAt(now)
                .expiresAt(expiration)
                .sign();
    }

}
