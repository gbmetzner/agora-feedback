package com.agora.domain.feedback.model.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Response containing comment author information.
 * <p>
 * Provides basic author details for comment responses.
 * </p>
 */
@Schema(
    name = "CommentAuthorResponse",
    description = "Author information for a comment",
    examples = "{\"id\": \"user-1\", \"username\": \"PlayerOne\"}"
)
public record CommentAuthorResponse(
        @Schema(description = "Unique author identifier", examples = "user-1")
        String id,

        @Schema(description = "Author username", examples = "PlayerOne")
        String username
) {
}
