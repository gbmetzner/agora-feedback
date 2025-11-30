package com.agora.domain.feedback.application.dto;

import com.agora.domain.feedback.model.entity.FeedbackStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Command for creating a new feedback item.
 * <p>
 * Contains all the necessary information to create a feedback submission including
 * title, description, category, author, sentiment, and tags.
 * </p>
 */
@Schema(
    name = "CreateFeedbackCommand",
    description = "Request to create a new feedback item",
    examples = "{\"title\": \"Dark mode support needed\", \"description\": \"Users are requesting a dark mode option for better usability in low-light environments. This would reduce eye strain and improve overall user experience.\", \"categoryId\": 117457749108987394, \"authorId\": 117457749108987388, \"sentiment\": \"POSITIVE\", \"tags\": \"ui,accessibility,enhancement\"}"
)
public record CreateFeedbackCommand(
        @Schema(description = "Feedback title", examples = "Dark mode support needed")
        @NotBlank(message = "Title cannot be blank")
        @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
        String title,

        @Schema(description = "Detailed feedback description", examples = "Users are requesting a dark mode option for better usability in low-light environments. This would reduce eye strain and improve overall user experience.")
        @NotBlank(message = "Description cannot be blank")
        @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
        String description,

        @Schema(description = "Category ID for feedback classification", examples = "117457749108987394")
        Long categoryId,

        @Schema(description = "Author/user ID who submitted the feedback", examples = "117457749108987388")
        Long authorId,

        @Schema(description = "Sentiment of the feedback", examples = "POSITIVE", enumeration = {"POSITIVE", "NEGATIVE", "NEUTRAL"})
        String sentiment,

        @Schema(description = "Comma-separated tags for categorization", examples = "ui,accessibility,enhancement")
        String tags
) {
}
