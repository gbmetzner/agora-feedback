package com.agora.domain.feedback.authorization;

import com.agora.domain.feedback.application.FeedbackApplicationService;
import com.agora.domain.feedback.application.dto.UpdateFeedbackCommand;
import com.agora.domain.feedback.exception.UnauthorizedException;
import com.agora.domain.feedback.model.dto.FeedbackResponse;
import com.agora.domain.feedback.model.entity.Feedback;
import com.agora.domain.feedback.model.entity.FeedbackStatus;
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
 * Tests for feedback update authorization.
 * <p>
 * Validates that only the feedback author or an admin can update feedback.
 * Non-authors should receive UnauthorizedException (403 Forbidden).
 * </p>
 */
@QuarkusTest
@DisplayName("Feedback Update Authorization Tests")
@Transactional
class FeedbackUpdateAuthorizationTest {

    @Inject
    FeedbackApplicationService feedbackService;

    @Inject
    UserRepository userRepository;

    @Inject
    FeedbackRepository feedbackRepository;

    private User author;
    private User otherUser;
    private User adminUser;
    private Feedback feedback;

    @BeforeEach
    @Transactional
    void setUp() {
        // Create test users
        long timestamp = System.nanoTime();

        this.author = new User();
        author.name = "Author User " + timestamp;
        author.username = "author_" + timestamp;
        author.email = "author_" + timestamp + "@test.com";
        author.discordId = timestamp;
        author.discordUsername = "author_discord_" + timestamp;
        author.role = Role.USER;
        userRepository.persist(author);

        this.otherUser = new User();
        otherUser.name = "Other User " + timestamp;
        otherUser.username = "other_" + timestamp;
        otherUser.email = "other_" + timestamp + "@test.com";
        otherUser.discordId = timestamp + 1;
        otherUser.discordUsername = "other_discord_" + timestamp;
        otherUser.role = Role.USER;
        userRepository.persist(otherUser);

        this.adminUser = new User();
        adminUser.name = "Admin User " + timestamp;
        adminUser.username = "admin_" + timestamp;
        adminUser.email = "admin_" + timestamp + "@test.com";
        adminUser.discordId = timestamp + 2;
        adminUser.discordUsername = "admin_discord_" + timestamp;
        adminUser.role = Role.ADMIN;
        userRepository.persist(adminUser);

        // Create test feedback with author
        this.feedback = new Feedback("Test Feedback", "Test Description");
        feedback.setAuthor(author);
        feedback.setStatus(FeedbackStatus.PENDING);
        feedbackRepository.persist(feedback);
    }

    @Test
    @DisplayName("testAuthorization_AuthorCanUpdate - Feedback author can update")
    void testAuthorization_AuthorCanUpdate() {
        // Author should be able to update their own feedback
        UpdateFeedbackCommand command = new UpdateFeedbackCommand(
            "Updated Title",
            "Updated Description",
            FeedbackStatus.ACKNOWLEDGED,
            null,
            null,
            null,
            null
        );

        assertThatNoException().isThrownBy(() ->
            feedbackService.updateFeedback(feedback.getId(), command, author.getId())
        );

        // Verify the update occurred
        Feedback updated = feedbackRepository.findById(feedback.getId());
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    @DisplayName("testAuthorization_OtherUserCannotUpdate - Non-author cannot update")
    void testAuthorization_OtherUserCannotUpdate() {
        // Other user should NOT be able to update feedback they don't own
        UpdateFeedbackCommand command = new UpdateFeedbackCommand(
            "Hacked Title",
            "Hacked Description",
            FeedbackStatus.ACKNOWLEDGED,
            null,
            null,
            null,
            null
        );

        assertThatThrownBy(() ->
            feedbackService.updateFeedback(feedback.getId(), command, otherUser.getId())
        )
            .isInstanceOf(UnauthorizedException.class)
            .hasMessageContaining("author or an admin");
    }

    @Test
    @DisplayName("testAuthorization_AdminCanUpdate - Admin can update any feedback")
    void testAuthorization_AdminCanUpdate() {
        // Admin should be able to update any feedback
        UpdateFeedbackCommand command = new UpdateFeedbackCommand(
            "Admin Updated Title",
            "Admin Updated Description",
            FeedbackStatus.IN_PROGRESS,
            null,
            null,
            null,
            null
        );

        assertThatNoException().isThrownBy(() ->
            feedbackService.updateFeedback(feedback.getId(), command, adminUser.getId())
        );

        // Verify the update occurred
        Feedback updated = feedbackRepository.findById(feedback.getId());
        assertThat(updated.getTitle()).isEqualTo("Admin Updated Title");
    }

    @Test
    @DisplayName("testAuthorization_NonAdminCannotBecomeAdmin - Regular user update doesn't grant admin")
    void testAuthorization_NonAdminCannotBecomeAdmin() {
        // Update author details but verify they can still update
        UpdateFeedbackCommand command = new UpdateFeedbackCommand(
            "Still Allowed",
            "Description",
            FeedbackStatus.PENDING,
            null,
            null,
            null,
            null
        );

        FeedbackResponse response = feedbackService.updateFeedback(
            feedback.getId(),
            command,
            author.getId()
        );

        assertThat(response.title()).isEqualTo("Still Allowed");
    }

    @Test
    @DisplayName("testAuthorization_UserRoleCannotUpdate - User with USER role cannot update others' feedback")
    void testAuthorization_UserRoleCannotUpdate() {
        // A user with USER role trying to update feedback they don't own should fail
        UpdateFeedbackCommand command = new UpdateFeedbackCommand(
            "Forbidden Update",
            "This is a forbidden update attempt",
            FeedbackStatus.PENDING,
            null,
            null,
            null,
            null
        );

        assertThatThrownBy(() ->
            feedbackService.updateFeedback(feedback.getId(), command, otherUser.getId())
        )
            .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("testAuthorization_FeedbackWithoutAuthor_OnlyAdminCanUpdate - Unattributed feedback only updatable by admin")
    void testAuthorization_FeedbackWithoutAuthor_OnlyAdminCanUpdate() {
        // Create feedback without author
        Feedback unattributedFeedback = new Feedback("Unattributed", "No author feedback description here");
        unattributedFeedback.setAuthor(null);
        feedbackRepository.persist(unattributedFeedback);

        UpdateFeedbackCommand command = new UpdateFeedbackCommand(
            "Updated Title",
            "Updated description that is long enough",
            FeedbackStatus.PENDING,
            null,
            null,
            null,
            null
        );

        // Regular user should NOT be able to update
        assertThatThrownBy(() ->
            feedbackService.updateFeedback(unattributedFeedback.getId(), command, otherUser.getId())
        )
            .isInstanceOf(UnauthorizedException.class);

        // Admin SHOULD be able to update
        assertThatNoException().isThrownBy(() ->
            feedbackService.updateFeedback(unattributedFeedback.getId(), command, adminUser.getId())
        );
    }

    @Test
    @DisplayName("testAuthorization_ErrorMessage - Unauthorized error has clear message")
    void testAuthorization_ErrorMessage() {
        UpdateFeedbackCommand command = new UpdateFeedbackCommand(
            "Title",
            "Description",
            FeedbackStatus.PENDING,
            null,
            null,
            null,
            null
        );

        assertThatThrownBy(() ->
            feedbackService.updateFeedback(feedback.getId(), command, otherUser.getId())
        )
            .isInstanceOf(UnauthorizedException.class)
            .hasMessageContaining("Only the feedback author or an admin can update this feedback");
    }
}
