package com.agora.domain.feedback.exception;

public class FeedbackNotFoundException extends DomainException {
    public FeedbackNotFoundException(Long id) {
        super("Feedback with id " + id + " not found");
    }
}
