package com.agora.domain.feedback.model.dto;

import lombok.Builder;

@Builder
public record CategoryResponse(String id, String name) {
}
