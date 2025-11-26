package com.agora.domain.user.infrastructure.discord;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DiscordUserResponse {

    public String id;
    public String username;
    public String discriminator;  // e.g., "0001"
    public String avatar;
    public String email;

    @JsonProperty("verified")
    public Boolean emailVerified;

    // Helper method
    public String getFullUsername() {
        return username + "#" + discriminator;
    }

    public String getAvatarUrl() {
        if (avatar == null) {
            return null;
        }
        return "https://cdn.discordapp.com/avatars/" + id + "/" + avatar + ".png";
    }
}
