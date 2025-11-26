package com.agora.domain.user.model.repository;

import com.agora.domain.user.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    public User findByDiscordId(String discordId) {
        return find("discordId", discordId).firstResult();
    }
}
