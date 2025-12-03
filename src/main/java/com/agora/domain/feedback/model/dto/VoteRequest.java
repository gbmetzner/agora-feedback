package com.agora.domain.feedback.model.dto;

import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Request to vote on feedback or comments.
 * <p>
 * Specifies the vote direction: up for upvote, down for downvote, none to remove vote.
 * </p>
 */
@Schema(
    name = "VoteRequest",
    description = "Request to vote on feedback or comments",
    examples = "{\"direction\": \"up\"}"
)
public record VoteRequest(
    @NotBlank(message = "Vote direction cannot be blank")
    @Schema(
        description = "Vote direction (up for upvote, down for downvote, none to remove vote)",
        examples = "up",
        enumeration = {"up", "down", "none"}
    )
    String direction
) {
}
