package com.agora.domain.feedback.fixture;

import com.agora.domain.feedback.model.entity.Comment;
import com.agora.domain.feedback.model.entity.Feedback;
import com.agora.domain.user.model.User;

/**
 * Fixtures for Comment entities.
 * <p>
 * Provides pre-configured Comment instances for use in testing feedback
 * discussions, responses, and comment threads.
 * </p>
 */
public class CommentFixtures {

    // ===== STANDARD COMMENTS =====

    public static Comment userComment(Feedback feedback) {
        Comment comment = new Comment(
            "This is a great feature idea!",
            feedback,
            UserFixtures.standardUser()
        );
        comment.setUpvotes(2);
        return comment;
    }

    public static Comment supportComment(Feedback feedback) {
        Comment comment = new Comment(
            "Thank you for reporting this issue. We are investigating.",
            feedback,
            UserFixtures.supportUser()
        );
        comment.setDeveloperResponse(true);
        comment.setUpvotes(5);
        return comment;
    }

    public static Comment developerComment(Feedback feedback) {
        Comment comment = new Comment(
            "We've fixed this issue in version 2.1.0. Please update and test.",
            feedback,
            UserFixtures.developerUser()
        );
        comment.setDeveloperResponse(true);
        comment.setUpvotes(12);
        return comment;
    }

    public static Comment adminComment(Feedback feedback) {
        Comment comment = new Comment(
            "Marking as completed. Please reopen if you still experience issues.",
            feedback,
            UserFixtures.adminUser()
        );
        comment.setDeveloperResponse(true);
        comment.setUpvotes(3);
        return comment;
    }

    // ===== COMMENTS WITH SPECIFIC PROPERTIES =====

    public static Comment commentWithText(Feedback feedback, String text) {
        return new Comment(
            text,
            feedback,
            UserFixtures.standardUser()
        );
    }

    public static Comment commentWithAuthor(Feedback feedback, User author) {
        return new Comment(
            "Test comment",
            feedback,
            author
        );
    }

    public static Comment developerResponseComment(Feedback feedback) {
        Comment comment = new Comment(
            "Official response from the development team",
            feedback,
            UserFixtures.developerUser()
        );
        comment.setDeveloperResponse(true);
        return comment;
    }

    public static Comment highlyUpvotedComment(Feedback feedback) {
        Comment comment = new Comment(
            "Everyone agrees with this comment",
            feedback,
            UserFixtures.standardUser()
        );
        comment.setUpvotes(50);
        return comment;
    }

    public static Comment minimumComment(Feedback feedback) {
        return new Comment(
            "OK",
            feedback,
            UserFixtures.standardUser()
        );
    }

    // ===== BULK TEST DATA =====

    public static Comment[] commentThread(Feedback feedback) {
        return new Comment[]{
            commentWithText(feedback, "Great feature idea"),
            supportComment(feedback),
            commentWithText(feedback, "Any timeline for this?"),
            developerComment(feedback),
            commentWithText(feedback, "Thank you for the quick fix!")
        };
    }

    public static Comment[] multiUserComments(Feedback feedback) {
        return new Comment[]{
            commentWithAuthor(feedback, UserFixtures.standardUser()),
            commentWithAuthor(feedback, UserFixtures.adminUser()),
            commentWithAuthor(feedback, UserFixtures.supportUser()),
            commentWithAuthor(feedback, UserFixtures.developerUser())
        };
    }
}
