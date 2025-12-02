package com.agora.domain.user.model;

/**
 * User roles for authorization and access control.
 * <p>
 * Defines the different roles that users can have in the system:
 * - ADMIN: Full system access, can perform all operations
 * - USER: Standard user role with basic permissions
 * - MODERATOR: Intermediate role with moderation capabilities (for future use)
 * </p>
 */
public enum Role {
    ADMIN("Admin"),
    MODERATOR("Moderator"),
    USER("User");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if this role has admin privileges
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
}
