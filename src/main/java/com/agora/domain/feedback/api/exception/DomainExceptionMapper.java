package com.agora.domain.feedback.api.exception;

import com.agora.domain.feedback.exception.CategoryNotFoundException;
import com.agora.domain.feedback.exception.DomainException;
import com.agora.domain.feedback.exception.FeedbackNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {

    @Override
    public Response toResponse(DomainException exception) {
        int status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

        if (exception instanceof FeedbackNotFoundException ||
            exception instanceof CategoryNotFoundException) {
            status = Response.Status.NOT_FOUND.getStatusCode();
        }

        ErrorResponse errorResponse = new ErrorResponse(status, exception.getMessage());
        return Response.status(status).entity(errorResponse).build();
    }
}
