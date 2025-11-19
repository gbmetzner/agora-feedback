package com.agora.domain.user.exception;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(Long id) {
        super("User with id " + id + " not found");
    }
}
