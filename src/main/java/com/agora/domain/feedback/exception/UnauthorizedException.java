package com.agora.domain.feedback.exception;

/**
 * Exception thrown when a user attempts an operation they are not authorized to perform.
 * <p>
 * This exception indicates that while the user is authenticated, they lack the necessary
 * permissions (roles/ownership) to perform the requested action.
 * </p>
 */
public class UnauthorizedException extends DomainException {

    /**
     * Create an UnauthorizedException with a message describing the unauthorized action.
     *
     * @param message A descriptive message about what authorization failed
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Create an UnauthorizedException with a message and cause.
     *
     * @param message A descriptive message about what authorization failed
     * @param cause The underlying cause exception
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
