package com.agora.domain.feedback.api.exception;

import com.agora.domain.feedback.exception.CategoryNotFoundException;
import com.agora.domain.feedback.exception.DomainException;
import com.agora.domain.feedback.exception.FeedbackNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * Exception mapper for feedback domain exceptions.
 * <p>
 * Maps domain-specific exceptions from the feedback domain to appropriate HTTP responses.
 * Handles specific not-found exceptions (FeedbackNotFoundException, CategoryNotFoundException)
 * by returning 404 status, and generic domain exceptions by returning 500 status.
 * </p>
 *
 * @author Agora Team
 * @version 1.0
 */
@Provider
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {
    private static final Logger LOGGER = Logger.getLogger(DomainExceptionMapper.class);

    /**
     * Converts a DomainException to an HTTP response.
     *
     * @param exception The domain exception to map
     * @return Response with appropriate HTTP status and error details
     */
    @Override
    public Response toResponse(DomainException exception) {
        int status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        String errorMessage = exception.getMessage();

        // Map specific not-found exceptions to 404
        if (exception instanceof FeedbackNotFoundException ||
            exception instanceof CategoryNotFoundException) {
            status = Response.Status.NOT_FOUND.getStatusCode();
        } else {
            // Log server errors for monitoring
            LOGGER.error("Unhandled domain exception", exception);
        }

        ErrorResponse errorResponse = ErrorResponse.of(status, errorMessage);
        return Response.status(status).entity(errorResponse).build();
    }
}
