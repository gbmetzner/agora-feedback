package com.agora.domain.user.resource;

import com.agora.domain.user.application.UserApplicationService;
import com.agora.domain.user.model.dto.LeaderboardEntry;
import com.agora.domain.user.model.dto.PaginatedLeaderboardResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * User-related REST endpoints
 */
@Path("/api/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Users", description = "User management and leaderboard")
public class UserResource {

    private static final Logger LOGGER = Logger.getLogger(UserResource.class);

    private final UserApplicationService userApplicationService;

    @Inject
    public UserResource(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    /**
     * Get paginated leaderboard ranked by reputation score
     */
    @GET
    @Path("/leaderboard")
    @Operation(
            summary = "Get user leaderboard",
            description = "Retrieve paginated leaderboard with users ranked by reputation score"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Paginated leaderboard",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = PaginatedLeaderboardResponse.class)
                    )
            )
    })
    public Response getLeaderboard(
            @Parameter(description = "Page number (1-indexed, default 1)", example = "1")
            @QueryParam("page") Integer page,
            @Parameter(description = "Page size (default 10, max 100)", example = "10")
            @QueryParam("pageSize") Integer size) {
        LOGGER.info("Retrieving leaderboard with pagination");

        int pageNum = page != null ? page : 1;
        int pageSize = size != null ? Math.min(size, 100) : 10;

        PaginatedLeaderboardResponse response = userApplicationService.getLeaderboard(pageNum, pageSize);
        return Response.ok(response).build();
    }

    /**
     * Get top N users by reputation score
     */
    @GET
    @Path("/leaderboard/top")
    @Operation(
            summary = "Get top users",
            description = "Retrieve top N users ranked by reputation score"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "List of top users",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = LeaderboardEntry.class)
                    )
            )
    })
    public Response getTopUsers(
            @Parameter(description = "Number of top users to retrieve (default 10, max 100)", example = "10")
            @QueryParam("limit") Integer limit) {
        LOGGER.info("Retrieving top users");

        int maxLimit = limit != null ? limit : 10;
        List<LeaderboardEntry> topUsers = userApplicationService.getTopUsers(maxLimit);
        return Response.ok(topUsers).build();
    }
}
