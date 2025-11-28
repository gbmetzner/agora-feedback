package com.agora.domain.feedback.api.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception mapper for validation constraint violations.
 * <p>
 * Handles Jakarta Bean Validation exceptions and converts them to a detailed
 * error response containing field-level validation errors. This provides
 * API consumers with specific information about which fields failed validation
 * and why.
 * </p>
 *
 * @author Agora Team
 * @version 1.0
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    private static final Logger LOGGER = Logger.getLogger(ValidationExceptionMapper.class);

    /**
     * Converts a validation exception to an HTTP response with field error details.
     *
     * @param exception The constraint violation exception from validation
     * @return Response with 400 Bad Request status and field validation errors
     */
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();

        // Extract field-level validation errors
        exception.getConstraintViolations().forEach(cv -> {
            String field = cv.getPropertyPath().toString();
            String message = cv.getMessage();
            fieldErrors.add(new ErrorResponse.FieldError(field, message));
        });

        LOGGER.debugf("Validation failed for %d fields", fieldErrors.size());

        ErrorResponse errorResponse = ErrorResponse.of(
                Response.Status.BAD_REQUEST.getStatusCode(),
                "Validation failed",
                fieldErrors
        );

        return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
    }
}
