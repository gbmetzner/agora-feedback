package com.agora.domain.feedback.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Standard error response for API failures.
 *
 * @param status HTTP status code
 * @param message Human-readable error message
 * @param timestamp When the error occurred
 * @param errors List of field validation errors (optional)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String message,
        OffsetDateTime timestamp,
        List<FieldError> errors
) {
    /**
     * Create an error response with current timestamp and no field errors.
     *
     * @param status HTTP status code
     * @param message Error message
     * @return ErrorResponse with current timestamp
     */
    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message, OffsetDateTime.now(), null);
    }

    /**
     * Create an error response with field validation errors.
     *
     * @param status HTTP status code
     * @param message Error message
     * @param errors Field validation errors
     * @return ErrorResponse with current timestamp and field errors
     */
    public static ErrorResponse of(int status, String message, List<FieldError> errors) {
        return new ErrorResponse(status, message, OffsetDateTime.now(), errors);
    }

    /**
     * Represents a single field validation error.
     *
     * @param field Name of the field that failed validation
     * @param message Validation error message
     */
    public record FieldError(String field, String message) {}
}
