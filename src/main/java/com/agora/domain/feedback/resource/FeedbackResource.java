package com.agora.domain.feedback.resource;

import com.agora.domain.feedback.application.FeedbackApplicationService;
import com.agora.domain.feedback.application.dto.CreateFeedbackCommand;
import com.agora.domain.feedback.application.dto.UpdateFeedbackCommand;
import com.agora.domain.feedback.common.IdHelper;
import com.agora.domain.feedback.exception.UnauthorizedException;
import com.agora.domain.feedback.model.dto.CategoryResponse;
import com.agora.domain.feedback.model.dto.CommentResponse;
import com.agora.domain.feedback.model.dto.CreateCommentRequest;
import com.agora.domain.feedback.model.dto.FeedbackResponse;
import com.agora.domain.feedback.model.dto.PaginatedFeedbackResponse;
import com.agora.domain.feedback.model.dto.VoteRequest;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Path("/api/v1/feedback")
@Tag(name = "Feedback", description = "Feedback submission, retrieval, and management")
@Authenticated
public class FeedbackResource {

    private static final Logger LOGGER = Logger.getLogger(FeedbackResource.class);

    private final FeedbackApplicationService feedbackApplicationService;
    private final JsonWebToken jwt;

    @Inject
    public FeedbackResource(FeedbackApplicationService feedbackApplicationService, JsonWebToken jwt) {
        this.feedbackApplicationService = feedbackApplicationService;
        this.jwt = jwt;
    }

    @GET
    @Operation(
            summary = "List all feedback items with pagination and sorting",
            description = "Retrieve feedback submissions with support for pagination and sorting by creation date"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Paginated list of feedback items",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = PaginatedFeedbackResponse.class)
                    )
            )
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAll(
            @Parameter(description = "Page number (1-indexed, default 1)", example = "1")
            @QueryParam("page") Integer page,
            @Parameter(description = "Page size (default 10, max 20)", example = "10")
            @QueryParam("pageSize") Integer size,
            @Parameter(description = "Sort order: 'newest' (default) or 'oldest'", example = "newest")
            @QueryParam("sortBy") String sort) {
        LOGGER.info("Listing feedback items with pagination and sorting");

        int pageNum = page != null ? page : 1;
        int pageSize = size != null ?  Math.min(size, 20) : 10;
        String sortOrder = sort != null ? sort : "newest";

        PaginatedFeedbackResponse response = feedbackApplicationService.getAllFeedbacksPaginated(pageNum, pageSize, sortOrder);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    @Operation(
            summary = "Get feedback by ID",
            description = "Retrieve a specific feedback item by its ID"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Feedback details",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = FeedbackResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Feedback not found"
            )
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
            @Parameter(description = "Feedback ID", required = true)
            @PathParam("id") String id) {
        FeedbackResponse response = feedbackApplicationService.getFeedback(IdHelper.toLong(id));
        return Response.ok(response).build();
    }

    @POST
    @Operation(
            summary = "Submit new feedback",
            description = "Create a new feedback item"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Feedback created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = FeedbackResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid request - validation errors"
            )
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(
            @Parameter(description = "Feedback creation data", required = true)
            CreateFeedbackCommand command) {
        var userId = jwt.getSubject();
        FeedbackResponse response = feedbackApplicationService.createFeedback(command, userId);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PATCH
    @Path("/{id}")
    @Operation(
            summary = "Update feedback",
            description = "Update an existing feedback item. Only the feedback author or an admin can update. Requires authentication via JWT token."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Feedback updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = FeedbackResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid request - validation errors"
            ),
            @APIResponse(
                    responseCode = "403",
                    description = "Forbidden - either not authenticated or user lacks permission to update this feedback"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Feedback not found"
            )
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(
            @Parameter(description = "Feedback ID", required = true)
            @PathParam("id") String id,
            @Parameter(description = "Updated feedback data", required = true)
            UpdateFeedbackCommand command) {
        // Require authentication
        if (jwt == null || jwt.getSubject() == null) {
            throw new UnauthorizedException("Authentication required to update feedback");
        }

        // Extract current user ID from JWT token
        Long currentUserId = IdHelper.toLong(jwt.getSubject());

        FeedbackResponse response = feedbackApplicationService.updateFeedback(
            IdHelper.toLong(id),
            command,
            currentUserId
        );
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(
            summary = "Delete feedback",
            description = "Delete a feedback item by ID"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Feedback deleted successfully"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Feedback not found"
            )
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
            @Parameter(description = "Feedback ID", required = true)
            @PathParam("id") String id) {
        feedbackApplicationService.deleteFeedback(IdHelper.toLong(id));
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/archive")
    @Operation(
            summary = "Archive feedback",
            description = "Mark a feedback item as archived, making it inactive without deletion"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Feedback archived successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = FeedbackResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Feedback not found"
            )
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response archive(
            @Parameter(description = "Feedback ID", required = true)
            @PathParam("id") String id) {
        var response = feedbackApplicationService.archiveFeedback(id);
        return Response.ok(response).build();
    }

    @POST
    @Path("/{id}/reopen")
    @Operation(
            summary = "Reopen feedback",
            description = "Reopen an archived feedback item, making it active again"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Feedback reopened successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = FeedbackResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Feedback not found"
            )
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response reopen(
            @Parameter(description = "Feedback ID", required = true)
            @PathParam("id") String id) {
        FeedbackResponse response = feedbackApplicationService.reopenFeedback(IdHelper.toLong(id));
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}/comments")
    @Operation(
            summary = "Get comments for feedback",
            description = "Retrieve all comments for a specific feedback item"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "List of comments retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = CommentResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Feedback not found"
            )
    })
    public Response getComments(
            @Parameter(description = "Feedback ID", required = true)
            @PathParam("id") String id) {
        var comments = feedbackApplicationService.getCommentsByFeedbackId(IdHelper.toLong(id));
        return Response.ok(comments).build();
    }

    @PUT
    @Path("/{id}/comments")
    @Operation(
            summary = "Add comment to feedback",
            description = "Add a new comment to an existing feedback item"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Comment created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = CommentResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid request - validation errors"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Feedback not found"
            )
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addComment(
            @Parameter(description = "Feedback ID", required = true)
            @PathParam("id") String id,
            @Parameter(description = "Comment data", required = true)
            CreateCommentRequest request) {
        CommentResponse response = feedbackApplicationService.addComment(IdHelper.toLong(id), request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/{id}/upvote")
    @Operation(
            summary = "Vote on feedback",
            description = "Upvote, downvote, or remove vote from feedback"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Vote recorded successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = FeedbackResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid vote direction"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Feedback not found"
            )
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response voteFeedback(
            @Parameter(description = "Feedback ID", required = true)
            @PathParam("id") String id,
            @Parameter(description = "Vote data", required = true)
            VoteRequest request) {
        FeedbackResponse response = feedbackApplicationService.voteFeedback(
            IdHelper.toLong(id),
            request.direction()
        );
        return Response.ok(response).build();
    }

    @POST
    @Path("/{id}/comments/{commentId}/upvote")
    @Operation(
            summary = "Vote on comment",
            description = "Upvote, downvote, or remove vote from comment"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Vote recorded successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = CommentResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid vote direction"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Comment or feedback not found"
            )
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response voteComment(
            @Parameter(description = "Feedback ID", required = true)
            @PathParam("id") String id,
            @Parameter(description = "Comment ID", required = true)
            @PathParam("commentId") String commentId,
            @Parameter(description = "Vote data", required = true)
            VoteRequest request) {
        CommentResponse response = feedbackApplicationService.voteComment(
            IdHelper.toLong(id),
            IdHelper.toLong(commentId),
            request.direction()
        );
        return Response.ok(response).build();
    }

    @GET
    @Path("/categories")
    @Operation(
            summary = "List all feedback categories",
            description = "Retrieve all available feedback categories"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "List of all feedback categories",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = CategoryResponse.class)
                    )
            )
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllCategories() {
        return Response.ok(feedbackApplicationService.findAllCategories()).build();
    }


}
