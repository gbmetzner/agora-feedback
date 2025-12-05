package com.agora.domain.user.model.dto;

import java.util.List;

/**
 * Paginated leaderboard response
 */
public record PaginatedLeaderboardResponse(
        List<LeaderboardEntry> entries,
        int currentPage,
        int pageSize,
        long totalUsers,
        int totalPages
) {
}
