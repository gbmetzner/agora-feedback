package com.agora.domain.feedback.model.entity;

import com.agora.domain.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Feedback entity domain logic.
 * <p>
 * Tests entity constructors, domain methods (archive, reopen, changeSentiment),
 * and state management without database persistence.
 * </p>
 */
@DisplayName("Feedback Entity Tests")
class FeedbackEntityTest {

    private Feedback feedback;
    private FeedbackCategory category;
    private User user;

    @BeforeEach
    void setUp() {
        feedback = new Feedback("Test Title", "This is a test feedback description");
        category = new FeedbackCategory("Bug Report");
        user = new User();
        user.name = "Test User";
    }

    // ===== CONSTRUCTOR TESTS =====

    @Test
    @DisplayName("testDefaultConstructor - Creates empty Feedback entity")
    void testDefaultConstructor() {
        // Act
        Feedback empty = new Feedback();

        // Assert
        assertThat(empty).isNotNull();
        assertThat(empty.getTitle()).isNull();
        assertThat(empty.getDescription()).isNull();
    }

    @Test
    @DisplayName("testParameterizedConstructor - Creates Feedback with title and description")
    void testParameterizedConstructor() {
        // Assert
        assertThat(feedback.getTitle()).isEqualTo("Test Title");
        assertThat(feedback.getDescription()).isEqualTo("This is a test feedback description");
    }

    // ===== DOMAIN METHOD: ARCHIVE TESTS =====

    @Test
    @DisplayName("testArchive_SetsArchivedTrue - Archive method sets archived flag to true")
    void testArchive_SetsArchivedTrue() {
        // Arrange
        assertThat(feedback.isArchived()).isFalse();

        // Act
        feedback.archive();

        // Assert
        assertThat(feedback.isArchived()).isTrue();
    }

    @Test
    @DisplayName("testArchive_CanArchiveMultipleTimes - Archive can be called multiple times")
    void testArchive_CanArchiveMultipleTimes() {
        // Act
        feedback.archive();
        feedback.archive();
        feedback.archive();

        // Assert
        assertThat(feedback.isArchived()).isTrue();
    }

    // ===== DOMAIN METHOD: REOPEN TESTS =====

    @Test
    @DisplayName("testReopen_ClearsArchivedFlag - Reopen method clears archived flag")
    void testReopen_ClearsArchivedFlag() {
        // Arrange
        feedback.archive();
        assertThat(feedback.isArchived()).isTrue();

        // Act
        feedback.reopen();

        // Assert
        assertThat(feedback.isArchived()).isFalse();
    }

    @Test
    @DisplayName("testReopen_ResetsCompletedStatusToPending - COMPLETED status reset to PENDING on reopen")
    void testReopen_ResetsCompletedStatusToPending() {
        // Arrange
        feedback.setStatus(FeedbackStatus.COMPLETED);
        feedback.archive();

        // Act
        feedback.reopen();

        // Assert
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.PENDING);
        assertThat(feedback.isArchived()).isFalse();
    }

    @Test
    @DisplayName("testReopen_PreservesNonCompletedStatus - Non-COMPLETED status is preserved on reopen")
    void testReopen_PreservesNonCompletedStatus() {
        // Arrange
        feedback.setStatus(FeedbackStatus.IN_PROGRESS);
        feedback.archive();

        // Act
        feedback.reopen();

        // Assert
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.IN_PROGRESS);
        assertThat(feedback.isArchived()).isFalse();
    }

    @Test
    @DisplayName("testReopen_WithPendingStatus - Reopen preserves PENDING status")
    void testReopen_WithPendingStatus() {
        // Arrange
        feedback.setStatus(FeedbackStatus.PENDING);
        feedback.archive();

        // Act
        feedback.reopen();

        // Assert
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.PENDING);
    }

    @Test
    @DisplayName("testReopen_WithAcknowledgedStatus - Reopen preserves ACKNOWLEDGED status")
    void testReopen_WithAcknowledgedStatus() {
        // Arrange
        feedback.setStatus(FeedbackStatus.ACKNOWLEDGED);
        feedback.archive();

        // Act
        feedback.reopen();

        // Assert
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.ACKNOWLEDGED);
    }

    // ===== DOMAIN METHOD: CHANGE SENTIMENT TESTS =====

    @Test
    @DisplayName("testChangeSentiment_SetsSentimentValue - ChangeSentiment updates sentiment field")
    void testChangeSentiment_SetsSentimentValue() {
        // Arrange
        assertThat(feedback.getSentiment()).isNull();

        // Act
        feedback.changeSentiment("Very Positive");

        // Assert
        assertThat(feedback.getSentiment()).isEqualTo("Very Positive");
    }

    @Test
    @DisplayName("testChangeSentiment_UpdatesExistingSentiment - ChangeSentiment updates existing value")
    void testChangeSentiment_UpdatesExistingSentiment() {
        // Arrange
        feedback.changeSentiment("Positive");
        assertThat(feedback.getSentiment()).isEqualTo("Positive");

        // Act
        feedback.changeSentiment("Negative");

        // Assert
        assertThat(feedback.getSentiment()).isEqualTo("Negative");
    }

    @Test
    @DisplayName("testChangeSentiment_CanSetToNull - ChangeSentiment can set sentiment to null")
    void testChangeSentiment_CanSetToNull() {
        // Arrange
        feedback.changeSentiment("Positive");

        // Act
        feedback.changeSentiment(null);

        // Assert
        assertThat(feedback.getSentiment()).isNull();
    }

    // ===== RELATIONSHIP TESTS =====

    @Test
    @DisplayName("testSetCategory_AssociatesCategory - Category relationship can be set")
    void testSetCategory_AssociatesCategory() {
        // Act
        feedback.setCategory(category);

        // Assert
        assertThat(feedback.getCategory()).isEqualTo(category);
        assertThat(feedback.getCategory().getName()).isEqualTo("Bug Report");
    }

    @Test
    @DisplayName("testSetAuthor_AssociatesAuthor - Author relationship can be set")
    void testSetAuthor_AssociatesAuthor() {
        // Act
        feedback.setAuthor(user);

        // Assert
        assertThat(feedback.getAuthor()).isEqualTo(user);
        assertThat(feedback.getAuthor().name).isEqualTo("Test User");
    }

    @Test
    @DisplayName("testSetCategoryAndAuthor_BothRelationships - Both category and author can coexist")
    void testSetCategoryAndAuthor_BothRelationships() {
        // Act
        feedback.setCategory(category);
        feedback.setAuthor(user);

        // Assert
        assertThat(feedback.getCategory()).isEqualTo(category);
        assertThat(feedback.getAuthor()).isEqualTo(user);
    }

    @Test
    @DisplayName("testClearCategory_SetsToNull - Category can be cleared")
    void testClearCategory_SetsToNull() {
        // Arrange
        feedback.setCategory(category);

        // Act
        feedback.setCategory(null);

        // Assert
        assertThat(feedback.getCategory()).isNull();
    }

    // ===== FIELD SETTER TESTS =====

    @Test
    @DisplayName("testSetTitle_UpdatesTitle - Title field can be updated")
    void testSetTitle_UpdatesTitle() {
        // Act
        feedback.setTitle("Updated Title");

        // Assert
        assertThat(feedback.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    @DisplayName("testSetDescription_UpdatesDescription - Description field can be updated")
    void testSetDescription_UpdatesDescription() {
        // Act
        feedback.setDescription("Updated description");

        // Assert
        assertThat(feedback.getDescription()).isEqualTo("Updated description");
    }

    @Test
    @DisplayName("testSetStatus_UpdatesStatus - Status field can be updated")
    void testSetStatus_UpdatesStatus() {
        // Act
        feedback.setStatus(FeedbackStatus.IN_PROGRESS);

        // Assert
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("testSetTags_UpdatesTags - Tags field can be updated")
    void testSetTags_UpdatesTags() {
        // Act
        feedback.setTags("tag1,tag2,tag3");

        // Assert
        assertThat(feedback.getTags()).isEqualTo("tag1,tag2,tag3");
    }

    @Test
    @DisplayName("testSetUpvotes_UpdatesUpvotes - Upvotes counter can be updated")
    void testSetUpvotes_UpdatesUpvotes() {
        // Arrange
        assertThat(feedback.getUpvotes()).isZero();

        // Act
        feedback.setUpvotes(10);

        // Assert
        assertThat(feedback.getUpvotes()).isEqualTo(10);
    }

    @Test
    @DisplayName("testSetDownvotes_UpdatesDownvotes - Downvotes counter can be updated")
    void testSetDownvotes_UpdatesDownvotes() {
        // Arrange
        assertThat(feedback.getDownvotes()).isZero();

        // Act
        feedback.setDownvotes(3);

        // Assert
        assertThat(feedback.getDownvotes()).isEqualTo(3);
    }

    @Test
    @DisplayName("testSetComments_UpdatesComments - Comments counter can be updated")
    void testSetComments_UpdatesComments() {
        // Arrange
        assertThat(feedback.getComments()).isZero();

        // Act
        feedback.setComments(5);

        // Assert
        assertThat(feedback.getComments()).isEqualTo(5);
    }

    // ===== STATUS TRANSITION TESTS =====

    @Test
    @DisplayName("testStatusTransitions - All status values are accessible")
    void testStatusTransitions() {
        // Act & Assert
        feedback.setStatus(FeedbackStatus.PENDING);
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.PENDING);

        feedback.setStatus(FeedbackStatus.ACKNOWLEDGED);
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.ACKNOWLEDGED);

        feedback.setStatus(FeedbackStatus.IN_PROGRESS);
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.IN_PROGRESS);

        feedback.setStatus(FeedbackStatus.COMPLETED);
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.COMPLETED);
    }

    // ===== ARCHIVE-REOPEN CYCLE TESTS =====

    @Test
    @DisplayName("testArchiveReopenCycle - Archive and reopen cycle works correctly")
    void testArchiveReopenCycle() {
        // Initial state
        assertThat(feedback.isArchived()).isFalse();

        // Archive
        feedback.archive();
        assertThat(feedback.isArchived()).isTrue();

        // Reopen
        feedback.reopen();
        assertThat(feedback.isArchived()).isFalse();

        // Archive again
        feedback.archive();
        assertThat(feedback.isArchived()).isTrue();
    }

    @Test
    @DisplayName("testComplexStateChanges - Complex state transitions work correctly")
    void testComplexStateChanges() {
        // Start with PENDING status
        feedback.setStatus(FeedbackStatus.PENDING);
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.PENDING);

        // Acknowledge
        feedback.setStatus(FeedbackStatus.ACKNOWLEDGED);
        feedback.changeSentiment("Positive");
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.ACKNOWLEDGED);
        assertThat(feedback.getSentiment()).isEqualTo("Positive");
        assertThat(feedback.isArchived()).isFalse();

        // Progress
        feedback.setStatus(FeedbackStatus.IN_PROGRESS);
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.IN_PROGRESS);

        // Complete
        feedback.setStatus(FeedbackStatus.COMPLETED);
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.COMPLETED);

        // Archive completed feedback
        feedback.archive();
        assertThat(feedback.isArchived()).isTrue();

        // Reopen (should reset to PENDING)
        feedback.reopen();
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.PENDING);
        assertThat(feedback.isArchived()).isFalse();
        assertThat(feedback.getSentiment()).isEqualTo("Positive");
    }
}
