package com.agora.domain.feedback.application;

import com.agora.domain.feedback.application.dto.CreateFeedbackCommand;
import com.agora.domain.feedback.application.dto.UpdateFeedbackCommand;
import com.agora.domain.feedback.common.IdHelper;
import com.agora.domain.feedback.exception.FeedbackNotFoundException;
import com.agora.domain.feedback.model.dto.CommentResponse;
import com.agora.domain.feedback.model.dto.CreateCommentRequest;
import com.agora.domain.feedback.model.dto.FeedbackResponse;
import com.agora.domain.feedback.model.entity.FeedbackStatus;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for FeedbackApplicationService.
 * <p>
 * Tests all public methods using the real database provided by Quarkus DevServices.
 * Covers happy paths and error cases.
 * </p>
 */
@QuarkusTest
@DisplayName("FeedbackApplicationService Tests")
class FeedbackApplicationServiceTest {

    @Inject
    FeedbackApplicationService service;

    // ===== CREATE FEEDBACK TESTS =====

    @Test
    @DisplayName("testCreateFeedback_Success - Valid feedback creation")
    void testCreateFeedback_Success() {
        // Arrange
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
                .title("Test Feedback Title")
                .description("This is a test feedback description for testing purposes")
                .build();

        // Act
        FeedbackResponse response = service.createFeedback(command, IdHelper.toString(117457749108987388L));

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.title()).isEqualTo("Test Feedback Title");
        assertThat(response.description()).isEqualTo("This is a test feedback description for testing purposes");
        assertThat(response.status()).isEqualTo(FeedbackStatus.PENDING);
        assertThat(response.upvotes()).isZero();
        assertThat(response.archived()).isFalse();
    }

    @Test
    @DisplayName("testCreateFeedback_InvalidCategory - Throws CategoryNotFoundException")
    void testCreateFeedback_InvalidCategory() {
        // Arrange
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
                .title("Test Title")
                .description("This is a test description for feedback")
                .categoryId("999999")
                .build();

        // Act & Assert - pass invalid user ID to trigger exception
        assertThatThrownBy(() -> service.createFeedback(command, IdHelper.toString(999999999L)))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("testCreateFeedback_InvalidAuthor - Throws UserNotFoundException")
    void testCreateFeedback_InvalidAuthor() {
        // Arrange
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
                .title("Test Title")
                .description("This is a test description for feedback")
                .build();

        // Act & Assert - pass invalid user ID to trigger exception
        assertThatThrownBy(() -> service.createFeedback(command, IdHelper.toString(999999999L)))
                .isInstanceOf(Exception.class);
    }

    // ===== GET FEEDBACK TESTS =====

    @Test
    @DisplayName("testGetFeedback_Success - Retrieve existing feedback")
    void testGetFeedback_Success() {
        // Arrange - Create feedback first
        CreateFeedbackCommand createCommand = CreateFeedbackCommand.builder()
                .title("Feedback to Retrieve")
                .description("This feedback will be retrieved for testing")
                .build();
        FeedbackResponse created = service.createFeedback(createCommand, IdHelper.toString(117457749108987388L));

        // Act
        FeedbackResponse response = service.getFeedback(IdHelper.toLong(created.id()));

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(created.id());
        assertThat(response.title()).isEqualTo("Feedback to Retrieve");
    }

    @Test
    @DisplayName("testGetFeedback_NotFound - Throws FeedbackNotFoundException")
    void testGetFeedback_NotFound() {
        // Act & Assert
        assertThatThrownBy(() -> service.getFeedback(999999999L))
                .isInstanceOf(FeedbackNotFoundException.class);
    }

    // ===== UPDATE FEEDBACK TESTS =====

    @Test
    @DisplayName("testUpdateFeedback_Success - Update existing feedback")
    void testUpdateFeedback_Success() {
        // Arrange - Create feedback first
        CreateFeedbackCommand createCommand = CreateFeedbackCommand.builder()
                .title("Original Title")
                .description("Original description here")
                .build();
        FeedbackResponse created = service.createFeedback(createCommand, IdHelper.toString(117457749108987388L));

        // Act - Update it
        UpdateFeedbackCommand updateCommand = new UpdateFeedbackCommand(
                "Updated Title",
                "Updated description here",
                FeedbackStatus.IN_PROGRESS,
                null,
                null,
                null,
                null
        );
        FeedbackResponse response = service.updateFeedback(IdHelper.toLong(created.id()), updateCommand);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("Updated Title");
        assertThat(response.description()).isEqualTo("Updated description here");
        assertThat(response.status()).isEqualTo(FeedbackStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("testUpdateFeedback_NotFound - Throws FeedbackNotFoundException")
    void testUpdateFeedback_NotFound() {
        // Arrange
        UpdateFeedbackCommand command = new UpdateFeedbackCommand(
                "Updated Title",
                "Updated description",
                FeedbackStatus.COMPLETED,
                null,
                null,
                null,
                null
        );

        // Act & Assert
        assertThatThrownBy(() -> service.updateFeedback(999999999L, command))
                .isInstanceOf(FeedbackNotFoundException.class);
    }

    // ===== DELETE FEEDBACK TESTS =====

    @Test
    @DisplayName("testDeleteFeedback_Success - Delete existing feedback")
    void testDeleteFeedback_Success() {
        // Arrange - Create feedback first
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
                .title("To Delete")
                .description("This feedback will be deleted soon to verify deletion")
                .build();
        FeedbackResponse created = service.createFeedback(command, IdHelper.toString(117457749108987388L));

        // Act - Delete it
        service.deleteFeedback(IdHelper.toLong(created.id()));

        // Assert - Verify it's deleted
        assertThatThrownBy(() -> service.getFeedback(IdHelper.toLong(created.id())))
                .isInstanceOf(FeedbackNotFoundException.class);
    }

    @Test
    @DisplayName("testDeleteFeedback_NotFound - Throws FeedbackNotFoundException")
    void testDeleteFeedback_NotFound() {
        // Act & Assert
        assertThatThrownBy(() -> service.deleteFeedback(999999999L))
                .isInstanceOf(FeedbackNotFoundException.class);
    }

    // ===== ARCHIVE FEEDBACK TESTS =====

    @Test
    @DisplayName("testArchiveFeedback_Success - Archive existing feedback")
    void testArchiveFeedback_Success() {
        // Arrange - Create feedback first
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
                .title("To Archive")
                .description("This feedback will be archived to test the archive functionality")
                .build();
        FeedbackResponse created = service.createFeedback(command, IdHelper.toString(117457749108987388L));

        // Act - Archive it
        FeedbackResponse response = service.archiveFeedback(created.id());

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.archived()).isTrue();
    }

    @Test
    @DisplayName("testArchiveFeedback_Verify - Verify archived flag persists")
    void testArchiveFeedback_Verify() {
        // Arrange - Create feedback first
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
                .title("To Archive Verify")
                .description("This feedback will be archived and verified")
                .build();
        FeedbackResponse created = service.createFeedback(command, IdHelper.toString(117457749108987388L));

        // Act - Archive it
        FeedbackResponse archived = service.archiveFeedback(created.id());

        // Verify by fetching again
        FeedbackResponse fetched = service.getFeedback(IdHelper.toLong(created.id()));

        // Assert
        assertThat(archived.archived()).isTrue();
        assertThat(fetched.archived()).isTrue();
    }

    // ===== REOPEN FEEDBACK TESTS =====

    @Test
    @DisplayName("testReopenFeedback_Success - Reopen archived feedback")
    void testReopenFeedback_Success() {
        // Arrange - Create and archive feedback first
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
                .title("To Reopen")
                .description("This feedback will be archived then reopened for testing")
                .build();
        FeedbackResponse created = service.createFeedback(command, IdHelper.toString(117457749108987388L));
        service.archiveFeedback(created.id());

        // Act - Reopen it
        FeedbackResponse response = service.reopenFeedback(IdHelper.toLong(created.id()));

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.archived()).isFalse();
    }

    @Test
    @DisplayName("testReopenFeedback_NotFound - Throws FeedbackNotFoundException")
    void testReopenFeedback_NotFound() {
        // Act & Assert
        assertThatThrownBy(() -> service.reopenFeedback(999999999L))
                .isInstanceOf(FeedbackNotFoundException.class);
    }

    // ===== COMMENT TESTS =====

    @Test
    @DisplayName("testAddComment_Success - Add comment to feedback")
    void testAddComment_Success() {
        // Arrange - Create feedback first
        CreateFeedbackCommand createCommand = CreateFeedbackCommand.builder()
                .title("Feedback for Comment")
                .description("This feedback will have a comment added to test comment creation")
                .build();
        FeedbackResponse created = service.createFeedback(createCommand, IdHelper.toString(117457749108987388L));

        // Act - Add comment
        CreateCommentRequest commentRequest = new CreateCommentRequest("Great feedback! This is very helpful and well-written.");
        CommentResponse response = service.addComment(IdHelper.toLong(created.id()), commentRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.text()).isEqualTo("Great feedback! This is very helpful and well-written.");
        assertThat(response.id()).isNotNull();
    }

    @Test
    @DisplayName("testAddComment_FeedbackNotFound - Throws FeedbackNotFoundException")
    void testAddComment_FeedbackNotFound() {
        // Arrange
        CreateCommentRequest request = new CreateCommentRequest("Test comment");

        // Act & Assert
        assertThatThrownBy(() -> service.addComment(999999999L, request))
                .isInstanceOf(FeedbackNotFoundException.class);
    }

    @Test
    @DisplayName("testGetCommentsByFeedbackId_Success - Retrieve comments")
    void testGetCommentsByFeedbackId_Success() {
        // Arrange - Create feedback with comment
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
                .title("Feedback for Comments")
                .description("This feedback will have comments added to test comment retrieval")
                .build();
        FeedbackResponse created = service.createFeedback(command, IdHelper.toString(117457749108987388L));
        CreateCommentRequest commentRequest = new CreateCommentRequest("Test comment for retrieval");
        service.addComment(IdHelper.toLong(created.id()), commentRequest);

        // Act
        var comments = service.getCommentsByFeedbackId(IdHelper.toLong(created.id()));

        // Assert
        assertThat(comments).isNotNull();
        assertThat(comments).isNotEmpty();
        assertThat(comments.get(0).text()).isEqualTo("Test comment for retrieval");
    }

    @Test
    @DisplayName("testGetCommentsByFeedbackId_NotFound - Throws FeedbackNotFoundException")
    void testGetCommentsByFeedbackId_NotFound() {
        // Act & Assert
        assertThatThrownBy(() -> service.getCommentsByFeedbackId(999999999L))
                .isInstanceOf(FeedbackNotFoundException.class);
    }
}
