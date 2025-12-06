package com.agora.domain.feedback.application;

import com.agora.domain.feedback.application.dto.CreateFeedbackCommand;
import com.agora.domain.feedback.common.IdHelper;
import com.agora.domain.feedback.model.dto.CommentResponse;
import com.agora.domain.feedback.model.dto.CreateCommentRequest;
import com.agora.domain.feedback.model.dto.FeedbackResponse;
import com.agora.domain.feedback.model.entity.Comment;
import com.agora.domain.feedback.model.entity.Feedback;
import com.agora.domain.feedback.model.repository.CommentRepository;
import com.agora.domain.feedback.model.repository.FeedbackRepository;
import com.agora.domain.user.model.Role;
import com.agora.domain.user.model.User;
import com.agora.domain.user.model.repository.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for comment voting functionality.
 * <p>
 * Validates upvoting and removing votes on comments.
 * Tests vote counter management and comment retrieval with updated vote counts.
 * </p>
 */
@QuarkusTest
@DisplayName("Comment Voting Tests")
@Transactional
class CommentVotingTest {

    @Inject
    FeedbackApplicationService feedbackService;

    @Inject
    FeedbackRepository feedbackRepository;

    @Inject
    CommentRepository commentRepository;

    @Inject
    UserRepository userRepository;

    private Feedback testFeedback;
    private Comment testComment;
    private User testUser;

    @BeforeEach
    @Transactional
    void setUp() {
        long timestamp = System.nanoTime();

        // Create test user
        testUser = new User();
        testUser.name = "Test User " + timestamp;
        testUser.username = "testuser_" + timestamp;
        testUser.email = "testuser_" + timestamp + "@test.com";
        testUser.discordId = timestamp;
        testUser.discordUsername = "testuser_discord_" + timestamp;
        testUser.role = Role.USER;
        userRepository.persist(testUser);

        // Create test feedback
        FeedbackResponse feedbackResponse = feedbackService.createFeedback(
            CreateFeedbackCommand.builder()
                    .title("Comment Test Feedback")
                    .description("This feedback is for testing comment voting").build(), IdHelper.toString(testUser.getId()));
        testFeedback = feedbackRepository.findById(IdHelper.toLong(feedbackResponse.id()));

        // Create test comment
        CommentResponse commentResponse = feedbackService.addComment(
            testFeedback.getId(),
            new CreateCommentRequest("Great feedback! This is very helpful.")
        );
        testComment = commentRepository.findById(IdHelper.toLong(commentResponse.id()));
    }

    @Test
    @DisplayName("testUpvoteComment - Upvoting increments comment upvotes counter")
    void testUpvoteComment() {
        // Upvote the comment
        CommentResponse voted = feedbackService.voteComment(
            testFeedback.getId(),
            testComment.getId(),
            "up"
        );

        // Verify upvotes incremented
        assertThat(voted.upvotes()).isEqualTo(1);
    }

    @Test
    @DisplayName("testMultipleUpvotes - Multiple upvotes increment counter correctly")
    void testMultipleUpvotes() {
        // Upvote multiple times
        CommentResponse after1 = feedbackService.voteComment(
            testFeedback.getId(),
            testComment.getId(),
            "up"
        );
        assertThat(after1.upvotes()).isEqualTo(1);

        CommentResponse after2 = feedbackService.voteComment(
            testFeedback.getId(),
            testComment.getId(),
            "up"
        );
        assertThat(after2.upvotes()).isEqualTo(2);

        CommentResponse after3 = feedbackService.voteComment(
            testFeedback.getId(),
            testComment.getId(),
            "up"
        );
        assertThat(after3.upvotes()).isEqualTo(3);
    }

    @Test
    @DisplayName("testRemoveCommentVote - Removing vote (none) decrements upvotes")
    void testRemoveCommentVote() {
        // Upvote
        CommentResponse upvoted = feedbackService.voteComment(
            testFeedback.getId(),
            testComment.getId(),
            "up"
        );
        assertThat(upvoted.upvotes()).isEqualTo(1);

        // Remove vote with "none"
        CommentResponse removed = feedbackService.voteComment(
            testFeedback.getId(),
            testComment.getId(),
            "none"
        );
        assertThat(removed.upvotes()).isEqualTo(0);
    }

    @Test
    @DisplayName("testDownvoteConvertedToRemoveUpvote - Downvote removes upvote on comments")
    void testDownvoteConvertedToRemoveUpvote() {
        // Upvote first
        CommentResponse upvoted = feedbackService.voteComment(
            testFeedback.getId(),
            testComment.getId(),
            "up"
        );
        assertThat(upvoted.upvotes()).isEqualTo(1);

        // Downvote (on comments, down also removes upvote)
        CommentResponse downvoted = feedbackService.voteComment(
            testFeedback.getId(),
            testComment.getId(),
            "down"
        );
        assertThat(downvoted.upvotes()).isEqualTo(0);
    }

    @Test
    @DisplayName("testVoteCountsPersisted - Comment vote counts are persisted to database")
    void testVoteCountsPersisted() {
        // Vote on comment
        feedbackService.voteComment(testFeedback.getId(), testComment.getId(), "up");
        feedbackService.voteComment(testFeedback.getId(), testComment.getId(), "up");

        // Retrieve from database directly
        Comment persisted = commentRepository.findById(testComment.getId());

        // Verify votes were persisted
        assertThat(persisted).isNotNull();
        assertThat(persisted.getUpvotes()).isEqualTo(2);
    }

    @Test
    @DisplayName("testVoteNonExistentComment - Voting on non-existent comment throws exception")
    void testVoteNonExistentComment() {
        // Try to vote on non-existent comment
        assertThatThrownBy(() ->
            feedbackService.voteComment(
                testFeedback.getId(),
                999999999L,
                "up"
            )
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Comment not found");
    }

    @Test
    @DisplayName("testVoteNonExistentFeedback - Voting with non-existent feedback throws exception")
    void testVoteNonExistentFeedback() {
        // Try to vote with non-existent feedback
        assertThatThrownBy(() ->
            feedbackService.voteComment(
                999999999L,
                testComment.getId(),
                "up"
            )
        )
            .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("testVoteCommentNotBelongingToFeedback - Comment from different feedback throws exception")
    void testVoteCommentNotBelongingToFeedback() {
        // Create another feedback
        FeedbackResponse otherFeedback = feedbackService.createFeedback(
            CreateFeedbackCommand.builder()
                .title("Other Feedback")
                .description("This is a different feedback item")
                .build(),
            IdHelper.toString(testUser.getId())
        );

        // Try to vote on comment from first feedback using second feedback ID
        assertThatThrownBy(() ->
            feedbackService.voteComment(
                IdHelper.toLong(otherFeedback.id()),
                testComment.getId(),
                "up"
            )
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("does not belong to this feedback");
    }

    @Test
    @DisplayName("testInvalidVoteDirection - Invalid direction throws exception")
    void testInvalidVoteDirection() {
        // Try invalid direction
        assertThatThrownBy(() ->
            feedbackService.voteComment(
                testFeedback.getId(),
                testComment.getId(),
                "invalid"
            )
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid vote direction");
    }

    @Test
    @DisplayName("testVoteCounterMinimum - Vote counter doesn't go below zero")
    void testVoteCounterMinimum() {
        // Try to remove upvote when counter is 0
        CommentResponse result = feedbackService.voteComment(
            testFeedback.getId(),
            testComment.getId(),
            "none"
        );

        // Should stay at 0, not go negative
        assertThat(result.upvotes()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("testNullVoteDirection - Null direction throws exception")
    void testNullVoteDirection() {
        // Try null direction
        assertThatThrownBy(() ->
            feedbackService.voteComment(
                testFeedback.getId(),
                testComment.getId(),
                null
            )
        )
            .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("testEmptyVoteDirection - Empty direction throws exception")
    void testEmptyVoteDirection() {
        // Try empty direction
        assertThatThrownBy(() ->
            feedbackService.voteComment(
                testFeedback.getId(),
                testComment.getId(),
                ""
            )
        )
            .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("testCommentVotesIndependent - Votes on different comments are independent")
    void testCommentVotesIndependent() {
        // Create second comment
        CommentResponse comment2Response = feedbackService.addComment(
            testFeedback.getId(),
            new CreateCommentRequest("Another great comment here!")
        );
        Comment comment2 = commentRepository.findById(IdHelper.toLong(comment2Response.id()));

        // Vote on first comment
        feedbackService.voteComment(testFeedback.getId(), testComment.getId(), "up");
        feedbackService.voteComment(testFeedback.getId(), testComment.getId(), "up");

        // Vote on second comment
        feedbackService.voteComment(testFeedback.getId(), comment2.getId(), "up");

        // Verify votes are independent
        Comment comment1Persisted = commentRepository.findById(testComment.getId());
        Comment comment2Persisted = commentRepository.findById(comment2.getId());

        assertThat(comment1Persisted.getUpvotes()).isEqualTo(2);
        assertThat(comment2Persisted.getUpvotes()).isEqualTo(1);
    }
}
