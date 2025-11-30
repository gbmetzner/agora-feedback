package com.agora.domain.feedback.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Request to create a new comment on feedback.
 * <p>
 * Contains the comment text to be posted.
 * </p>
 */
@Schema(
    name = "CreateCommentRequest",
    description = "Request to create a new comment on feedback",
    examples = "{\"text\": \"Great feedback! This is a valuable feature request that many users have been asking for.\"}"
)
public record CreateCommentRequest(
        @Schema(
            description = "Comment text content",
            examples = "Great feedback! This is a valuable feature request that many users have been asking for.",
            minLength = 1,
            maxLength = 5000
        )
        @NotBlank(message = "Comment text cannot be blank")
        @Size(min = 1, max = 5000, message = "Comment must be between 1 and 5000 characters")
        String text
) {
}
