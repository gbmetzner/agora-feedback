package com.agora.domain.feedback.fixture;

import com.agora.domain.feedback.model.entity.Feedback;
import com.agora.domain.feedback.model.entity.FeedbackCategory;
import com.agora.domain.feedback.model.entity.FeedbackStatus;
import com.agora.domain.user.model.User;

/**
 * Fixtures for Feedback entities.
 * <p>
 * Provides pre-configured Feedback instances representing common scenarios:
 * pending issues, acknowledged items, in-progress work, and completed feedback.
 * </p>
 */
public class FeedbackFixtures {

    // ===== BASIC FEEDBACK ITEMS =====

    public static Feedback pendingBugReport() {
        Feedback feedback = new Feedback(
            "Application crashes on startup",
            "When I launch the application, it immediately crashes with no error message"
        );
        feedback.setStatus(FeedbackStatus.PENDING);
        feedback.setCategory(CategoryFixtures.bugReport());
        feedback.setAuthor(UserFixtures.standardUser());
        feedback.setSentiment("Negative");
        feedback.setUpvotes(5);
        return feedback;
    }

    public static Feedback acknowledgedFeatureRequest() {
        Feedback feedback = new Feedback(
            "Add dark mode support",
            "Would really appreciate a dark mode option for the UI"
        );
        feedback.setStatus(FeedbackStatus.ACKNOWLEDGED);
        feedback.setCategory(CategoryFixtures.featureRequest());
        feedback.setAuthor(UserFixtures.standardUser());
        feedback.setSentiment("Positive");
        feedback.setUpvotes(12);
        return feedback;
    }

    public static Feedback inProgressEnhancement() {
        Feedback feedback = new Feedback(
            "Improve search performance",
            "Search is slow when querying large datasets"
        );
        feedback.setStatus(FeedbackStatus.IN_PROGRESS);
        feedback.setCategory(CategoryFixtures.enhancement());
        feedback.setAuthor(UserFixtures.adminUser());
        feedback.setSentiment("Neutral");
        feedback.setUpvotes(8);
        return feedback;
    }

    public static Feedback completedFeedback() {
        Feedback feedback = new Feedback(
            "Fix database connection timeout",
            "Database connections sometimes timeout unexpectedly"
        );
        feedback.setStatus(FeedbackStatus.COMPLETED);
        feedback.setCategory(CategoryFixtures.bugReport());
        feedback.setAuthor(UserFixtures.standardUser());
        feedback.setSentiment("Positive");
        feedback.setUpvotes(3);
        return feedback;
    }

    // ===== FEEDBACK WITH SPECIFIC STATES =====

    public static Feedback archivedFeedback() {
        Feedback feedback = new Feedback(
            "Obsolete feature request",
            "This is no longer relevant"
        );
        feedback.setStatus(FeedbackStatus.PENDING);
        feedback.setCategory(CategoryFixtures.other());
        feedback.setAuthor(UserFixtures.standardUser());
        feedback.archive();
        return feedback;
    }

    public static Feedback highlyUpvotedFeedback() {
        Feedback feedback = new Feedback(
            "Popular feature request",
            "Everyone wants this feature"
        );
        feedback.setStatus(FeedbackStatus.ACKNOWLEDGED);
        feedback.setCategory(CategoryFixtures.featureRequest());
        feedback.setAuthor(UserFixtures.standardUser());
        feedback.setUpvotes(150);
        feedback.setDownvotes(2);
        return feedback;
    }

    public static Feedback controversialFeedback() {
        Feedback feedback = new Feedback(
            "Controversial change proposal",
            "This change would break backward compatibility"
        );
        feedback.setStatus(FeedbackStatus.IN_PROGRESS);
        feedback.setCategory(CategoryFixtures.enhancement());
        feedback.setAuthor(UserFixtures.adminUser());
        feedback.setUpvotes(45);
        feedback.setDownvotes(40);
        feedback.setSentiment("Neutral");
        return feedback;
    }

    public static Feedback feedbackWithComments() {
        Feedback feedback = new Feedback(
            "Frequently discussed topic",
            "This has generated lots of discussion"
        );
        feedback.setStatus(FeedbackStatus.IN_PROGRESS);
        feedback.setCategory(CategoryFixtures.featureRequest());
        feedback.setAuthor(UserFixtures.standardUser());
        feedback.setUpvotes(10);
        feedback.setComments(25);
        return feedback;
    }

    // ===== FEEDBACK WITH SPECIFIC PROPERTIES =====

    public static Feedback feedbackWithStatus(FeedbackStatus status) {
        Feedback feedback = new Feedback("Test Feedback", "Test description");
        feedback.setStatus(status);
        feedback.setAuthor(UserFixtures.standardUser());
        return feedback;
    }

    public static Feedback feedbackWithCategory(FeedbackCategory category) {
        Feedback feedback = new Feedback("Test Feedback", "Test description");
        feedback.setCategory(category);
        feedback.setAuthor(UserFixtures.standardUser());
        return feedback;
    }

    public static Feedback feedbackWithAuthor(User author) {
        Feedback feedback = new Feedback("Test Feedback", "Test description");
        feedback.setAuthor(author);
        return feedback;
    }

    public static Feedback feedbackWithSentiment(String sentiment) {
        Feedback feedback = new Feedback("Test Feedback", "Test description");
        feedback.setSentiment(sentiment);
        feedback.setAuthor(UserFixtures.standardUser());
        return feedback;
    }

    public static Feedback feedbackWithTags(String tags) {
        Feedback feedback = new Feedback("Test Feedback", "Test description");
        feedback.setTags(tags);
        feedback.setAuthor(UserFixtures.standardUser());
        return feedback;
    }

    // ===== BULK TEST DATA =====

    public static Feedback[] stateTransitionSequence() {
        return new Feedback[]{
            feedbackWithStatus(FeedbackStatus.PENDING),
            feedbackWithStatus(FeedbackStatus.ACKNOWLEDGED),
            feedbackWithStatus(FeedbackStatus.IN_PROGRESS),
            feedbackWithStatus(FeedbackStatus.COMPLETED)
        };
    }

    public static Feedback[] categorizedFeedback() {
        return new Feedback[]{
            feedbackWithCategory(CategoryFixtures.bugReport()),
            feedbackWithCategory(CategoryFixtures.featureRequest()),
            feedbackWithCategory(CategoryFixtures.enhancement()),
            feedbackWithCategory(CategoryFixtures.documentation())
        };
    }
}
