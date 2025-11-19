package com.agora.domain.feedback.exception;

public class CategoryNotFoundException extends DomainException {
    public CategoryNotFoundException(Long id) {
        super("Category with id " + id + " not found");
    }
}
