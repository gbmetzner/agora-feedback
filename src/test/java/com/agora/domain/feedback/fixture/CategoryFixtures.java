package com.agora.domain.feedback.fixture;

import com.agora.domain.feedback.model.entity.FeedbackCategory;

/**
 * Fixtures for FeedbackCategory entities.
 * <p>
 * Provides pre-configured FeedbackCategory instances representing common
 * feedback types or problem domains.
 * </p>
 */
public class CategoryFixtures {

    public static FeedbackCategory bugReport() {
        return new FeedbackCategory("Bug Report");
    }

    public static FeedbackCategory featureRequest() {
        return new FeedbackCategory("Feature Request");
    }

    public static FeedbackCategory enhancement() {
        return new FeedbackCategory("Enhancement");
    }

    public static FeedbackCategory documentation() {
        return new FeedbackCategory("Documentation");
    }

    public static FeedbackCategory performance() {
        return new FeedbackCategory("Performance");
    }

    public static FeedbackCategory usability() {
        return new FeedbackCategory("Usability");
    }

    public static FeedbackCategory security() {
        return new FeedbackCategory("Security");
    }

    public static FeedbackCategory other() {
        return new FeedbackCategory("Other");
    }

    public static FeedbackCategory categoryWithName(String name) {
        return new FeedbackCategory(name);
    }

    public static FeedbackCategory categoryWithId(Long id, String name) {
        return new FeedbackCategory(id, name);
    }
}
