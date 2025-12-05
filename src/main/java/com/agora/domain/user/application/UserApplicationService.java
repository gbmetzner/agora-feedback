package com.agora.domain.user.application;

import com.agora.domain.feedback.common.IdHelper;
import com.agora.domain.user.model.User;
import com.agora.domain.user.model.dto.LeaderboardEntry;
import com.agora.domain.user.model.dto.PaginatedLeaderboardResponse;
import com.agora.domain.user.model.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

/**
 * Application service for user management operations
 */
@ApplicationScoped
public class UserApplicationService {

    private final UserRepository userRepository;

    @Inject
    public UserApplicationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get leaderboard with pagination, ranked by reputation score
     *
     * @param pageNumber The page number (1-indexed, defaults to 1)
     * @param pageSize The number of items per page (defaults to 10, max 100)
     * @return Paginated leaderboard response
     */
    @Transactional
    public PaginatedLeaderboardResponse getLeaderboard(int pageNumber, int pageSize) {
        // Validate inputs
        int page = Math.max(1, pageNumber);
        int size = Math.max(1, Math.min(pageSize, 100)); // Max 100 items per page

        // Get users ranked by reputation (0-indexed for Panache)
        List<User> users = userRepository.findTopByReputation((page - 1) * size + size);

        // Manually paginate the results
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, users.size());
        List<User> pageUsers = users.subList(startIndex, endIndex);

        // Convert to leaderboard entries
        List<LeaderboardEntry> entries = pageUsers.stream()
                .map(this::toLeaderboardEntry)
                .toList();

        // Get total count
        long totalUsers = userRepository.count();
        int totalPages = (int) Math.ceil((double) totalUsers / size);

        return new PaginatedLeaderboardResponse(entries, page, size, totalUsers, totalPages);
    }

    /**
     * Get top N users by reputation
     *
     * @param limit Maximum number of users to return (default 10, max 100)
     * @return List of top users
     */
    @Transactional
    public List<LeaderboardEntry> getTopUsers(int limit) {
        int maxLimit = Math.max(1, Math.min(limit, 100)); // Cap at 100
        List<User> topUsers = userRepository.findTopByReputation(maxLimit);
        return topUsers.stream()
                .map(this::toLeaderboardEntry)
                .toList();
    }

    /**
     * Convert User to LeaderboardEntry
     */
    private LeaderboardEntry toLeaderboardEntry(User user) {
        return new LeaderboardEntry(
                IdHelper.toString(user.getId()),
                user.username,
                user.name,
                user.reputationScore,
                user.avatarUrl
        );
    }
}
