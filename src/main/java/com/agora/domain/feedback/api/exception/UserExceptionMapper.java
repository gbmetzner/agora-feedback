package com.agora.domain.feedback.api.exception;

import com.agora.domain.user.exception.DomainException;
import com.agora.domain.user.exception.UserNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UserExceptionMapper implements ExceptionMapper<DomainException> {

    @Override
    public Response toResponse(DomainException exception) {
        int status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

        if (exception instanceof UserNotFoundException) {
            status = Response.Status.NOT_FOUND.getStatusCode();
        }

        ErrorResponse errorResponse = new ErrorResponse(status, exception.getMessage());
        return Response.status(status).entity(errorResponse).build();
    }
}
