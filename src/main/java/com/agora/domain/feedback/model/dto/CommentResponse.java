package com.agora.domain.feedback.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.OffsetDateTime;

/**
 * Response containing a comment on feedback.
 * <p>
 * Represents a comment with author information, engagement metrics, and timestamps.
 * </p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
    name = "CommentResponse",
    description = "Feedback comment details response",
    examples = "{\"id\": \"117457749108987475\", \"author\": {\"id\": \"user-1\", \"username\": \"PlayerOne\"}, \"content\": \"Great feedback!\", \"isDeveloperResponse\": false, \"upvotes\": 5, \"createdAt\": \"2024-01-15T10:30:00Z\", \"updatedAt\": \"2024-01-15T10:30:00Z\"}"
)
public record CommentResponse(
        @Schema(description = "Unique comment identifier", examples = "117457749108987475")
        String id,

        @Schema(description = "Author information")
        CommentAuthorResponse author,

        @JsonProperty("content")
        @Schema(description = "Comment content text", examples = "Great feedback!")
        String text,

        @Schema(description = "Whether this is an official developer response", examples = "false")
        boolean isDeveloperResponse,

        @Schema(description = "Number of upvotes on this comment", examples = "5")
        int upvotes,

        @Schema(description = "Timestamp when comment was created", examples = "2024-01-15T10:30:00Z")
        OffsetDateTime createdAt,

        @Schema(description = "Timestamp when comment was last updated", examples = "2024-01-15T10:30:00Z")
        OffsetDateTime updatedAt
) {
}