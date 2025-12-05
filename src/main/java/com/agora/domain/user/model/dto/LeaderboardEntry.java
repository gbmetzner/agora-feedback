package com.agora.domain.user.model.dto;

/**
 * Leaderboard entry for user ranking
 */
public record LeaderboardEntry(
        String userId,
        String username,
        String displayName,
        Integer reputationScore,
        String avatarUrl
) {
}
