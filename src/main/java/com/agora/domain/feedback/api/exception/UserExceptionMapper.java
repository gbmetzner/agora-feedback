package com.agora.domain.feedback.api.exception;

import com.agora.domain.user.exception.DomainException;
import com.agora.domain.user.exception.UserNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * Exception mapper for user domain exceptions.
 * <p>
 * Maps domain-specific exceptions from the user domain to appropriate HTTP responses.
 * Handles UserNotFoundException by returning 404 status, and generic user domain
 * exceptions by returning 500 status.
 * </p>
 * <p>
 * Note: This mapper handles exceptions from the user domain package
 * (com.agora.domain.user.exception), separate from feedback domain exceptions.
 * </p>
 *
 * @author Agora Team
 * @version 1.0
 */
@Provider
public class UserExceptionMapper implements ExceptionMapper<DomainException> {
    private static final Logger LOGGER = Logger.getLogger(UserExceptionMapper.class);

    /**
     * Converts a user domain exception to an HTTP response.
     *
     * @param exception The user domain exception to map
     * @return Response with appropriate HTTP status and error details
     */
    @Override
    public Response toResponse(DomainException exception) {
        int status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        String errorMessage = exception.getMessage();

        // Map specific not-found exceptions to 404
        if (exception instanceof UserNotFoundException) {
            status = Response.Status.NOT_FOUND.getStatusCode();
        } else {
            // Log server errors for monitoring
            LOGGER.error("Unhandled user domain exception", exception);
        }

        ErrorResponse errorResponse = ErrorResponse.of(status, errorMessage);
        return Response.status(status).entity(errorResponse).build();
    }
}
