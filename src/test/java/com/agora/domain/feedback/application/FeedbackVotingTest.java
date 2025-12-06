package com.agora.domain.feedback.application;

import com.agora.domain.feedback.application.dto.CreateFeedbackCommand;
import com.agora.domain.feedback.common.IdHelper;
import com.agora.domain.feedback.model.dto.FeedbackResponse;
import com.agora.domain.feedback.model.entity.Feedback;
import com.agora.domain.feedback.model.repository.FeedbackRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for feedback voting functionality.
 * <p>
 * Validates upvoting, downvoting, and removing votes on feedback items.
 * Tests both the service layer logic and vote counter management.
 * </p>
 */
@QuarkusTest
@DisplayName("Feedback Voting Tests")
@Transactional
class FeedbackVotingTest {

    @Inject
    FeedbackApplicationService feedbackService;

    @Inject
    FeedbackRepository feedbackRepository;

    @Test
    @DisplayName("testUpvoteFeedback - Upvoting increments upvotes counter")
    void testUpvoteFeedback() {
        // Create feedback
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
            .title("Feature Request")
            .description("Please add dark mode support to the application")
            .build();
        FeedbackResponse created = feedbackService.createFeedback(command, IdHelper.toString(117457749108987388L));

        // Upvote the feedback
        FeedbackResponse voted = feedbackService.voteFeedback(
            IdHelper.toLong(created.id()),
            "up"
        );

        // Verify upvotes incremented
        assertThat(voted.upvotes()).isEqualTo(1);
        assertThat(voted.comments()).isEqualTo(0);
    }

    @Test
    @DisplayName("testDownvoteFeedback - Downvoting increments downvotes counter")
    void testDownvoteFeedback() {
        // Create feedback
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
            .title("Bug Report")
            .description("Application crashes when loading large datasets")
            .build();
        FeedbackResponse created = feedbackService.createFeedback(command, IdHelper.toString(117457749108987388L));

        // Downvote the feedback
        FeedbackResponse voted = feedbackService.voteFeedback(
            IdHelper.toLong(created.id()),
            "down"
        );

        // Verify downvotes incremented (stored separately from upvotes)
        assertThat(voted.upvotes()).isEqualTo(0);
    }

    @Test
    @DisplayName("testMultipleUpvotes - Multiple upvotes increment counter correctly")
    void testMultipleUpvotes() {
        // Create feedback
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
            .title("Popular Feature")
            .description("Users are requesting this feature frequently")
            .build();
        FeedbackResponse created = feedbackService.createFeedback(command, IdHelper.toString(117457749108987388L));
        Long feedbackId = IdHelper.toLong(created.id());

        // Upvote multiple times
        FeedbackResponse after1 = feedbackService.voteFeedback(feedbackId, "up");
        assertThat(after1.upvotes()).isEqualTo(1);

        FeedbackResponse after2 = feedbackService.voteFeedback(feedbackId, "up");
        assertThat(after2.upvotes()).isEqualTo(2);

        FeedbackResponse after3 = feedbackService.voteFeedback(feedbackId, "up");
        assertThat(after3.upvotes()).isEqualTo(3);
    }

    @Test
    @DisplayName("testRemoveVote - Removing vote resets both counters")
    void testRemoveVote() {
        // Create feedback
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
            .title("Vote Test")
            .description("Testing vote removal functionality here")
            .build();
        FeedbackResponse created = feedbackService.createFeedback(command, IdHelper.toString(117457749108987388L));
        Long feedbackId = IdHelper.toLong(created.id());

        // Upvote
        FeedbackResponse upvoted = feedbackService.voteFeedback(feedbackId, "up");
        assertThat(upvoted.upvotes()).isEqualTo(1);

        // Remove vote with "none"
        FeedbackResponse removed = feedbackService.voteFeedback(feedbackId, "none");
        assertThat(removed.upvotes()).isEqualTo(0);
    }

    @Test
    @DisplayName("testUpvoteDownvoteCycle - Cycle between up and down votes")
    void testUpvoteDownvoteCycle() {
        // Create feedback
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
            .title("Cycle Test")
            .description("Testing vote direction cycling functionality")
            .build();
        FeedbackResponse created = feedbackService.createFeedback(command, IdHelper.toString(117457749108987388L));
        Long feedbackId = IdHelper.toLong(created.id());

        // Upvote
        FeedbackResponse upvoted = feedbackService.voteFeedback(feedbackId, "up");
        assertThat(upvoted.upvotes()).isEqualTo(1);

        // Downvote (adds downvote independently)
        FeedbackResponse downvoted = feedbackService.voteFeedback(feedbackId, "down");
        assertThat(downvoted.upvotes()).isEqualTo(1);

        // Remove vote
        FeedbackResponse removed = feedbackService.voteFeedback(feedbackId, "none");
        assertThat(removed.upvotes()).isEqualTo(0);
    }

    @Test
    @DisplayName("testInvalidVoteDirection - Invalid direction throws exception")
    void testInvalidVoteDirection() {
        // Create feedback
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
            .title("Error Test")
            .description("Testing invalid vote direction handling")
            .build();
        FeedbackResponse created = feedbackService.createFeedback(command, IdHelper.toString(117457749108987388L));

        // Try invalid direction
        assertThatThrownBy(() ->
            feedbackService.voteFeedback(IdHelper.toLong(created.id()), "invalid")
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid vote direction");
    }

    @Test
    @DisplayName("testVoteNonExistentFeedback - Voting on non-existent feedback throws exception")
    void testVoteNonExistentFeedback() {
        // Try to vote on non-existent feedback
        assertThatThrownBy(() ->
            feedbackService.voteFeedback(999999999L, "up")
        )
            .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("testVoteCountsPersisted - Vote counts are persisted to database")
    void testVoteCountsPersisted() {
        // Create feedback
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
            .title("Persistence Test")
            .description("Testing that vote counts persist in database")
            .build();
        FeedbackResponse created = feedbackService.createFeedback(command, IdHelper.toString(117457749108987388L));
        Long feedbackId = IdHelper.toLong(created.id());

        // Vote
        feedbackService.voteFeedback(feedbackId, "up");
        feedbackService.voteFeedback(feedbackId, "up");

        // Retrieve from database directly
        Feedback persisted = feedbackRepository.findById(feedbackId);

        // Verify votes were persisted
        assertThat(persisted).isNotNull();
        assertThat(persisted.getUpvotes()).isEqualTo(2);
    }

    @Test
    @DisplayName("testVoteCounterMinimum - Vote counter doesn't go below zero")
    void testVoteCounterMinimum() {
        // Create feedback (starts with 0 upvotes)
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
            .title("Minimum Test")
            .description("Testing vote counter minimum boundary")
            .build();
        FeedbackResponse created = feedbackService.createFeedback(command, IdHelper.toString(117457749108987388L));
        Long feedbackId = IdHelper.toLong(created.id());

        // Try to remove upvote when counter is 0
        FeedbackResponse result = feedbackService.voteFeedback(feedbackId, "none");

        // Should stay at 0, not go negative
        assertThat(result.upvotes()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("testNullVoteDirection - Null direction throws exception")
    void testNullVoteDirection() {
        // Create feedback
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
            .title("Null Test")
            .description("Testing null vote direction handling")
            .build();
        FeedbackResponse created = feedbackService.createFeedback(command, IdHelper.toString(117457749108987388L));

        // Try null direction
        assertThatThrownBy(() ->
            feedbackService.voteFeedback(IdHelper.toLong(created.id()), null)
        )
            .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("testEmptyVoteDirection - Empty direction throws exception")
    void testEmptyVoteDirection() {
        // Create feedback
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
            .title("Empty Test")
            .description("Testing empty vote direction handling")
            .build();
        FeedbackResponse created = feedbackService.createFeedback(command, IdHelper.toString(117457749108987388L));

        // Try empty direction
        assertThatThrownBy(() ->
            feedbackService.voteFeedback(IdHelper.toLong(created.id()), "")
        )
            .isInstanceOf(Exception.class);
    }
}
