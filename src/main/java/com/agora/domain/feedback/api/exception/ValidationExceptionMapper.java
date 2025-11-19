package com.agora.domain.feedback.api.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.ArrayList;
import java.util.List;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        exception.getConstraintViolations().forEach(cv -> {
            String field = cv.getPropertyPath().toString();
            String message = cv.getMessage();
            fieldErrors.add(new ErrorResponse.FieldError(field, message));
        });

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
        errorResponse.setMessage("Validation failed");
        errorResponse.setErrors(fieldErrors);
        errorResponse.setTimestamp(java.time.OffsetDateTime.now());

        return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
    }
}
