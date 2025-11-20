package com.agora.domain.feedback.model.dto;

import com.agora.domain.feedback.model.entity.FeedbackStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FeedbackResponse(
        String id,
        String title,
        String description,
        FeedbackStatus status,
        Long categoryId,
        String categoryName,
        Long authorId,
        String authorName,
        String sentiment,
        String tags,
        OffsetDateTime createdAt,
        Boolean archived
) {
}
