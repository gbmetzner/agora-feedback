package com.agora.domain.user.infrastructure.discord;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * REST client for Discord API
 */
@RegisterRestClient(configKey = "discord-api")
@Path("/")
public interface DiscordApiClient {

    /**
     * Exchange authorization code for access token
     */
    @POST
    @Path("/oauth2/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    DiscordTokenResponse exchangeCode(
            @FormParam("client_id") String clientId,
            @FormParam("client_secret") String clientSecret,
            @FormParam("grant_type") String grantType,
            @FormParam("code") String code,
            @FormParam("redirect_uri") String redirectUri
    );

    /**
     * Get user info from Discord
     */
    @GET
    @Path("/users/@me")
    @Produces(MediaType.APPLICATION_JSON)
    DiscordUserResponse getUserInfo( @HeaderParam("Authorization") String authorization    );
}
