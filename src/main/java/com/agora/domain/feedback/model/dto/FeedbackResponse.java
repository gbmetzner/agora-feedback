package com.agora.domain.feedback.model.dto;

import com.agora.domain.feedback.model.entity.FeedbackStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FeedbackResponse(
        String id,
        String title,
        String description,
        String sentiment,
        int upvotes,
        int comments,
        FeedbackStatus status,
        String categoryName,
        String authorName,
        OffsetDateTime createdAt,
        Boolean archived
) {
}
