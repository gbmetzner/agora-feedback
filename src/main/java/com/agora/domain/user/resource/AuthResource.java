package com.agora.domain.user.resource;

import com.agora.domain.auth.DiscordAuthService;
import com.agora.domain.user.dto.AuthResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;

/**
 * OAuth2 authentication endpoints
 */
@Path("/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    DiscordAuthService authService;

    /**
     * Discord callback endpoint
     * Discord redirects here after user authorizes
     */
    @GET
    @Path("/discord/callback")
    public Response handleCallback(@QueryParam("code") String code, @QueryParam("error") String error) {

        if (error != null) {
            // User denied authorization
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authorization denied: " + error).build();
        }

        if (code == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing authorization code").build();
        }

        // Exchange code for JWT
        var authResponse = authService.authenticate(code);
        String frontendUrl = "http://localhost:3000/auth/callback?token=" + authResponse.token;
        return Response.temporaryRedirect(URI.create(frontendUrl)).build();
    }

}
