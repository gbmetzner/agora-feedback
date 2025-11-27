package com.agora.domain.user.model;

import com.agora.domain.feedback.common.IdHelper;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "\"user\"")
public class User extends PanacheEntityBase {
    @Id
    private Long id;

    @NotBlank(message = "User name cannot be blank")
    @Size(min = 2, max = 255, message = "User name must be between 2 and 255 characters")
    public String name;
    @Column(unique = true, nullable = false, length = 100)
    public String username;

    @Column(unique = true, nullable = false)
    public String email;

    @Column(name = "reputation_score", nullable = false)
    public Integer reputationScore = 0;

    // Discord-specific fields
    @Column(name = "discord_id", unique = true)
    public Long discordId;

    @Column(name = "discord_username")
    public String discordUsername;

    @Column(name = "avatar_url")
    public String avatarUrl;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = IdHelper.generateId();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
