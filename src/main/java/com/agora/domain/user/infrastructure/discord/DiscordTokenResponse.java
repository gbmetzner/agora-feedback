package com.agora.domain.user.infrastructure.discord;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DiscordTokenResponse {

    @JsonProperty("access_token")
    public String accessToken;

    @JsonProperty("token_type")
    public String tokenType;

    @JsonProperty("expires_in")
    public Integer expiresIn;

    @JsonProperty("refresh_token")
    public String refreshToken;

    public String scope;
}
