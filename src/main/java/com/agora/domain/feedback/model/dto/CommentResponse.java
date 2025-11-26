package com.agora.domain.feedback.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommentResponse(
        String id,
        CommentAuthorResponse author,
        @JsonProperty("content")
        String text,
        boolean isDeveloperResponse,
        int upvotes,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}