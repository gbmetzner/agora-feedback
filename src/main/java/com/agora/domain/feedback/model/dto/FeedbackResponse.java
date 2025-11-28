package com.agora.domain.feedback.model.dto;

import com.agora.domain.feedback.model.entity.FeedbackStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.OffsetDateTime;

/**
 * Response containing feedback item details.
 * <p>
 * Represents a feedback submission with all its metadata including status,
 * engagement metrics, and categorization information.
 * </p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
    name = "FeedbackResponse",
    description = "Feedback item details response",
    example = "{\"id\": \"117457749108987399\", \"title\": \"Dark mode support needed\", \"description\": \"Users are requesting a dark mode option for better usability in low-light environments.\", \"sentiment\": \"POSITIVE\", \"upvotes\": 45, \"comments\": 8, \"status\": \"ACKNOWLEDGED\", \"categoryName\": \"Feature Request\", \"authorName\": \"Bob Smith\", \"createdAt\": \"2025-11-20T11:30:00+00:00\", \"archived\": false}"
)
public record FeedbackResponse(
        @Schema(description = "Unique feedback identifier", example = "117457749108987399")
        String id,

        @Schema(description = "Feedback title", example = "Dark mode support needed")
        String title,

        @Schema(description = "Detailed feedback description", example = "Users are requesting a dark mode option for better usability in low-light environments.")
        String description,

        @Schema(description = "Sentiment of the feedback", example = "POSITIVE", enumeration = {"POSITIVE", "NEGATIVE", "NEUTRAL"})
        String sentiment,

        @Schema(description = "Number of upvotes", example = "45")
        int upvotes,

        @Schema(description = "Number of comments", example = "8")
        int comments,

        @Schema(description = "Current status of the feedback", example = "ACKNOWLEDGED", enumeration = {"PENDING", "ACKNOWLEDGED", "IN_PROGRESS", "COMPLETED"})
        FeedbackStatus status,

        @Schema(description = "Category name for this feedback", example = "Feature Request")
        String categoryName,

        @Schema(description = "Name of the feedback author", example = "Bob Smith")
        String authorName,

        @Schema(description = "Timestamp when feedback was created", example = "2025-11-20T11:30:00+00:00")
        OffsetDateTime createdAt,

        @Schema(description = "Whether the feedback is archived", example = "false")
        Boolean archived
) {
}
