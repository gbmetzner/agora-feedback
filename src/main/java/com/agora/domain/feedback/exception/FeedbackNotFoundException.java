package com.agora.domain.feedback.exception;

import com.agora.domain.feedback.common.IdHelper;

public class FeedbackNotFoundException extends DomainException {
    public FeedbackNotFoundException(String id) {
        super("Feedback with id " + id + " not found");
    }
    public FeedbackNotFoundException(Long id) {
        super("Feedback with id " + IdHelper.toString(id) + " not found");
    }
}
