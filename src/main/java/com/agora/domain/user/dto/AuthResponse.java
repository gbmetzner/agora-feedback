package com.agora.domain.user.dto;

/**
 * Response after successful authentication
 */
public class AuthResponse {
    public String token;        // Internal JWT
    public String userId;
    public String username;
    public String email;
    public String avatarUrl;
}
