package com.agora.domain.feedback.application.dto;

import com.agora.domain.feedback.model.entity.FeedbackStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateFeedbackCommand(
        @NotBlank(message = "Title cannot be blank")
        @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
        String title,

        @NotBlank(message = "Description cannot be blank")
        @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
        String description,

        @NotNull(message = "Status is required")
        FeedbackStatus status,

        Long categoryId,

        Long authorId,

        String sentiment,

        String tags
) {
}
