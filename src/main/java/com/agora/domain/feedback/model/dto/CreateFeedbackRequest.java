package com.agora.domain.feedback.model.dto;

import com.agora.domain.feedback.model.entity.FeedbackStatus;

public record CreateFeedbackRequest(
        String title,
        String description,
        FeedbackStatus status,
        Long categoryId,
        Long authorId,
        String sentiment,
        String tags
) {
}
