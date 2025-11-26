package com.agora.domain.feedback.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotBlank(message = "Comment text cannot be blank")
        @Size(min = 1, max = 5000, message = "Comment must be between 1 and 5000 characters")
        String text
) {
}
