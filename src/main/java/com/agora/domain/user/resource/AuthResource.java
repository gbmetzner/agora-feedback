package com.agora.domain.user.resource;

import com.agora.domain.auth.DiscordAuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.net.URI;

/**
 * OAuth2 authentication endpoints for Discord integration
 */
@Path("/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "OAuth2 authentication and authorization")
public class AuthResource {

    private static final Logger LOGGER = Logger.getLogger(AuthResource.class);


    @Inject
    DiscordAuthService authService;

    /**
     * Discord OAuth2 callback endpoint
     * Handles the redirect from Discord after user authorizes the application
     */
    @GET
    @Path("/discord/callback")
    @Operation(
            summary = "Discord OAuth2 callback",
            description = "Handles Discord OAuth2 callback after user authorization. Exchanges authorization code for JWT token and redirects to frontend."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "307",
                    description = "Temporary redirect to frontend with JWT token"
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Bad Request - missing authorization code"
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Unauthorized - user denied authorization or invalid code"
            )
    })
    public Response handleCallback(
            @Parameter(description = "Discord OAuth2 authorization code", required = false)
            @QueryParam("code") String code,
            @Parameter(description = "Error parameter if user denied authorization", required = false)
            @QueryParam("error") String error) {

        if (error != null) {
            // User denied authorization
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authorization denied: " + error).build();
        }

        if (code == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing authorization code").build();
        }

        // Exchange code for JWT
        var authResponse = authService.authenticate(code);
        LOGGER.info("Token: " + authResponse.token);
        var frontendUrl = "http://localhost:3000/auth/callback?token=" + authResponse.token;
        return Response.temporaryRedirect(URI.create(frontendUrl)).build();
    }

}
