package com.agora.domain.auth;

import com.agora.domain.feedback.common.IdHelper;
import com.agora.domain.user.dto.AuthResponse;
import com.agora.domain.user.infrastructure.discord.DiscordApiClient;
import com.agora.domain.user.infrastructure.discord.DiscordTokenResponse;
import com.agora.domain.user.infrastructure.discord.DiscordUserResponse;
import com.agora.domain.user.infrastructure.security.JwtService;
import com.agora.domain.user.model.User;
import com.agora.domain.user.model.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

/**
 * Handles OAuth2 flow with Discord
 */
@ApplicationScoped
public class DiscordAuthService {

    private static final Logger LOG = Logger.getLogger(DiscordAuthService.class);

   private final DiscordApiClient discordClient;
   private final UserRepository userRepository;
   private final JwtService jwtService;

    @ConfigProperty(name = "discord.oauth.client-id")
    String clientId;

    @ConfigProperty(name = "discord.oauth.client-secret")
    String clientSecret;

    @ConfigProperty(name = "discord.oauth.redirect-uri")
    String redirectUri;

    @Inject
    public DiscordAuthService(@RestClient DiscordApiClient discordClient, UserRepository userRepository, JwtService jwtService) {
        this.discordClient = discordClient;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    /**
     * Exchange Discord authorization code for internal JWT
     */
    @Transactional
    public AuthResponse authenticate(String code) {
        var token = exchangeCodeForToken(code);
        var discordUser = getUserFromDiscord(token);
        var user = createOrUpdateUser(discordUser);
        return generateAuthResponse(user);
    }

    /**
     * Step 1: Exchange authorization code for Discord access token
     */
    private DiscordTokenResponse exchangeCodeForToken(String code) {
        return discordClient.exchangeCode(
                clientId,
                clientSecret,
                "authorization_code",
                code,
                redirectUri
        );
    }

    /**
     * Step 2: Get user info from Discord using access token
     */
    private DiscordUserInfo getUserFromDiscord(DiscordTokenResponse tokenResponse) {
        String authHeader = tokenResponse.tokenType + " " + tokenResponse.accessToken;
        var discordUser = discordClient.getUserInfo(authHeader);
        return new DiscordUserInfo(discordUser, tokenResponse.accessToken);
    }

    /**
     * Step 3: Create or update user in our database
     */
    private User createOrUpdateUser(DiscordUserInfo info) {
        var existingUser = userRepository.findByDiscordId(info.discordUser.id);
        if (existingUser != null) {
            return updateExistingUser(existingUser, info.discordUser);
        }
        return createNewUser(info.discordUser);
    }

    private User updateExistingUser(User user, DiscordUserResponse discordUser) {
        user.discordUsername = discordUser.getFullUsername();
        user.avatarUrl = discordUser.getAvatarUrl();
        user.email = discordUser.email;

        userRepository.persistAndFlush(user);

        return user;
    }

    private User createNewUser(DiscordUserResponse discordUser) {
        User user = new User();
        user.discordId = discordUser.id;
        user.username = discordUser.username;
        user.discordUsername = discordUser.getFullUsername();
        user.email = discordUser.email;
        user.avatarUrl = discordUser.getAvatarUrl();
        user.reputationScore = 0;

        userRepository.persistAndFlush(user);

        return user;
    }

    /**
     * Step 4: Generate internal JWT
     */
    private AuthResponse generateAuthResponse(User user) {
        String jwt = jwtService.generateToken(
                IdHelper.toString(user.getId()),
                user.username,
                user.email
        );

        AuthResponse response = new AuthResponse();
        response.token = jwt;
        response.userId = IdHelper.toString(user.getId());
        response.username = user.username;
        response.email = user.email;
        response.avatarUrl = user.avatarUrl;

        return response;
    }

    // Helper classes
    private static record DiscordUserInfo (
        DiscordUserResponse discordUser,
        String accessToken){
    }
}
