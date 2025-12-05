package com.agora.domain.user.model.repository;

import com.agora.domain.user.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    public User findByDiscordId(Long discordId) {
        return find("discordId", discordId).firstResult();
    }

    /**
     * Get users ranked by reputation score (descending)
     * @param page The page number (0-indexed)
     * @param pageSize The number of items per page
     * @return List of users ordered by reputation score
     */
    public List<User> findLeaderboard(int page, int pageSize) {
        return findAll()
                .page(Page.of(page, pageSize))
                .list();
    }

    /**
     * Get top N users by reputation score
     * @param limit Maximum number of users to return
     * @return List of top users by reputation
     */
    public List<User> findTopByReputation(int limit) {
        return find("ORDER BY reputationScore DESC")
                .page(Page.of(0, limit))
                .list();
    }
}
