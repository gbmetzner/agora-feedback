package com.agora.domain.feedback.fixture;

import com.agora.domain.user.model.User;

/**
 * Fixtures for User entities.
 * <p>
 * Provides pre-configured User instances for use across tests.
 * Each fixture represents a common user scenario or role.
 * </p>
 */
public class UserFixtures {

    // ===== STANDARD USERS =====

    public static User standardUser() {
        User user = new User();
        user.name = "John Doe";
        user.username = "john_doe_" + System.nanoTime();
        user.email = "john_" + System.nanoTime() + "@example.com";
        user.discordId = 111111111111111111L;
        user.discordUsername = "JohnDoe#1234";
        return user;
    }

    public static User adminUser() {
        User user = new User();
        user.name = "Admin User";
        user.username = "admin_" + System.nanoTime();
        user.email = "admin_" + System.nanoTime() + "@example.com";
        user.discordId = 222222222222222222L;
        user.discordUsername = "AdminUser#9999";
        return user;
    }

    public static User supportUser() {
        User user = new User();
        user.name = "Support Team";
        user.username = "support_" + System.nanoTime();
        user.email = "support_" + System.nanoTime() + "@example.com";
        user.discordId = 333333333333333333L;
        user.discordUsername = "Support#5555";
        return user;
    }

    public static User developerUser() {
        User user = new User();
        user.name = "Developer";
        user.username = "developer_" + System.nanoTime();
        user.email = "dev_" + System.nanoTime() + "@example.com";
        user.discordId = 444444444444444444L;
        user.discordUsername = "Developer#0000";
        return user;
    }

    // ===== USERS WITH SPECIFIC PROPERTIES =====

    public static User userWithName(String name) {
        User user = new User();
        user.name = name;
        user.username = "user_" + System.nanoTime();
        user.email = "user_" + System.nanoTime() + "@example.com";
        return user;
    }

    public static User userWithEmail(String email) {
        User user = new User();
        user.name = "Test User";
        user.username = "user_" + System.nanoTime();
        user.email = email;
        return user;
    }

    public static User userWithDiscordId(Long discordId) {
        User user = new User();
        user.name = "Discord User";
        user.username = "user_" + System.nanoTime();
        user.email = "user_" + System.nanoTime() + "@example.com";
        user.discordId = discordId;
        return user;
    }
}
