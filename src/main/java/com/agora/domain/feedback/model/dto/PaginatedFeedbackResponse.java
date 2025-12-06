package com.agora.domain.feedback.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PaginatedFeedbackResponse(
        @JsonProperty("data")
        List<FeedbackResponse> items,

        @JsonProperty("page")
        int currentPage,

        @JsonProperty("page_size")
        int pageSize,

        @JsonProperty("total_items")
        long totalItems,

        @JsonProperty("total_pages")
        int totalPages
) {
}
