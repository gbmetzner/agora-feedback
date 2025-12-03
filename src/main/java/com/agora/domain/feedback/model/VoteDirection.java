package com.agora.domain.feedback.model;

/**
 * Enumeration representing the direction of a vote.
 * <p>
 * Defines the possible vote directions for feedback and comments:
 * - UP: Upvote (positive vote)
 * - DOWN: Downvote (negative vote)
 * - NONE: Remove vote (neutral, cancels previous vote)
 * </p>
 */
public enum VoteDirection {
    UP("up"),
    DOWN("down"),
    NONE("none");

    private final String value;

    VoteDirection(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Parse a string value to VoteDirection enum
     *
     * @param value the string value (case-insensitive)
     * @return the corresponding VoteDirection
     * @throws IllegalArgumentException if value doesn't match any direction
     */
    public static VoteDirection fromString(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Vote direction cannot be null or empty");
        }
        return switch (value.toLowerCase()) {
            case "up" -> UP;
            case "down" -> DOWN;
            case "none" -> NONE;
            default -> throw new IllegalArgumentException("Invalid vote direction: " + value);
        };
    }
}
