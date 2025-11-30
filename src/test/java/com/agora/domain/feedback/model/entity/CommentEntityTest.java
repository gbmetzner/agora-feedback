package com.agora.domain.feedback.model.entity;

import com.agora.domain.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Comment entity.
 * <p>
 * Tests entity constructors, relationships, and field management without database persistence.
 * </p>
 */
@DisplayName("Comment Entity Tests")
class CommentEntityTest {

    private Feedback feedback;
    private User author;
    private Comment comment;

    @BeforeEach
    void setUp() {
        feedback = new Feedback("Test Feedback", "This is test feedback");
        author = new User();
        author.name = "Test Author";
        comment = new Comment("Test comment text", feedback, author);
    }

    // ===== CONSTRUCTOR TESTS =====

    @Test
    @DisplayName("testDefaultConstructor - Creates empty Comment")
    void testDefaultConstructor() {
        // Act
        Comment empty = new Comment();

        // Assert
        assertThat(empty).isNotNull();
        assertThat(empty.getText()).isNull();
        assertThat(empty.getFeedback()).isNull();
        assertThat(empty.getAuthor()).isNull();
    }

    @Test
    @DisplayName("testParameterizedConstructor - Creates Comment with text, feedback, and author")
    void testParameterizedConstructor() {
        // Assert
        assertThat(comment.getText()).isEqualTo("Test comment text");
        assertThat(comment.getFeedback()).isEqualTo(feedback);
        assertThat(comment.getAuthor()).isEqualTo(author);
    }

    // ===== RELATIONSHIP TESTS =====

    @Test
    @DisplayName("testFeedbackRelationship - Comment maintains feedback reference")
    void testFeedbackRelationship() {
        // Assert
        assertThat(comment.getFeedback()).isNotNull();
        assertThat(comment.getFeedback()).isEqualTo(feedback);
        assertThat(comment.getFeedback().getTitle()).isEqualTo("Test Feedback");
    }

    @Test
    @DisplayName("testAuthorRelationship - Comment maintains author reference")
    void testAuthorRelationship() {
        // Assert
        assertThat(comment.getAuthor()).isNotNull();
        assertThat(comment.getAuthor()).isEqualTo(author);
        assertThat(comment.getAuthor().name).isEqualTo("Test Author");
    }

    @Test
    @DisplayName("testSetFeedback_UpdatesFeedback - Feedback can be changed")
    void testSetFeedback_UpdatesFeedback() {
        // Arrange
        Feedback newFeedback = new Feedback("Another Feedback", "Different description");

        // Act
        comment.setFeedback(newFeedback);

        // Assert
        assertThat(comment.getFeedback()).isEqualTo(newFeedback);
        assertThat(comment.getFeedback().getTitle()).isEqualTo("Another Feedback");
    }

    @Test
    @DisplayName("testSetAuthor_UpdatesAuthor - Author can be changed")
    void testSetAuthor_UpdatesAuthor() {
        // Arrange
        User newAuthor = new User();
        newAuthor.name = "New Author";

        // Act
        comment.setAuthor(newAuthor);

        // Assert
        assertThat(comment.getAuthor()).isEqualTo(newAuthor);
        assertThat(comment.getAuthor().name).isEqualTo("New Author");
    }

    // ===== TEXT FIELD TESTS =====

    @Test
    @DisplayName("testSetText_UpdatesText - Text field can be updated")
    void testSetText_UpdatesText() {
        // Act
        comment.setText("Updated comment text");

        // Assert
        assertThat(comment.getText()).isEqualTo("Updated comment text");
    }

    @Test
    @DisplayName("testSetText_Multiple - Text can be updated multiple times")
    void testSetText_Multiple() {
        // Act
        comment.setText("First update");
        assertThat(comment.getText()).isEqualTo("First update");

        comment.setText("Second update");
        assertThat(comment.getText()).isEqualTo("Second update");

        comment.setText("Final update");
        assertThat(comment.getText()).isEqualTo("Final update");
    }

    // ===== DEVELOPER RESPONSE FLAG TESTS =====

    @Test
    @DisplayName("testSetDeveloperResponse_True - Developer response flag can be set to true")
    void testSetDeveloperResponse_True() {
        // Arrange
        assertThat(comment.isDeveloperResponse()).isFalse();

        // Act
        comment.setDeveloperResponse(true);

        // Assert
        assertThat(comment.isDeveloperResponse()).isTrue();
    }

    @Test
    @DisplayName("testSetDeveloperResponse_False - Developer response flag can be set to false")
    void testSetDeveloperResponse_False() {
        // Arrange
        comment.setDeveloperResponse(true);

        // Act
        comment.setDeveloperResponse(false);

        // Assert
        assertThat(comment.isDeveloperResponse()).isFalse();
    }

    @Test
    @DisplayName("testSetDeveloperResponse_Toggle - Developer response flag can be toggled")
    void testSetDeveloperResponse_Toggle() {
        // Act & Assert
        assertThat(comment.isDeveloperResponse()).isFalse();

        comment.setDeveloperResponse(true);
        assertThat(comment.isDeveloperResponse()).isTrue();

        comment.setDeveloperResponse(false);
        assertThat(comment.isDeveloperResponse()).isFalse();

        comment.setDeveloperResponse(true);
        assertThat(comment.isDeveloperResponse()).isTrue();
    }

    // ===== UPVOTES FIELD TESTS =====

    @Test
    @DisplayName("testSetUpvotes_UpdatesUpvotes - Upvotes count can be set")
    void testSetUpvotes_UpdatesUpvotes() {
        // Act
        comment.setUpvotes(10);

        // Assert
        assertThat(comment.getUpvotes()).isEqualTo(10);
    }

    @Test
    @DisplayName("testSetUpvotes_IncreasesUpvotes - Upvotes can be incremented")
    void testSetUpvotes_IncreasesUpvotes() {
        // Act
        comment.setUpvotes(5);
        comment.setUpvotes(10);
        comment.setUpvotes(20);

        // Assert
        assertThat(comment.getUpvotes()).isEqualTo(20);
    }

    @Test
    @DisplayName("testSetUpvotes_Zero - Upvotes can be reset to zero")
    void testSetUpvotes_Zero() {
        // Arrange
        comment.setUpvotes(10);

        // Act
        comment.setUpvotes(0);

        // Assert
        assertThat(comment.getUpvotes()).isZero();
    }

    // ===== MULTIPLE COMMENTS TESTS =====

    @Test
    @DisplayName("testMultipleComments_Independent - Multiple comments are independent")
    void testMultipleComments_Independent() {
        // Arrange
        Comment comment1 = new Comment("Comment 1", feedback, author);
        Comment comment2 = new Comment("Comment 2", feedback, author);
        Comment comment3 = new Comment("Comment 3", feedback, author);

        // Act & Assert
        assertThat(comment1.getText()).isEqualTo("Comment 1");
        assertThat(comment2.getText()).isEqualTo("Comment 2");
        assertThat(comment3.getText()).isEqualTo("Comment 3");

        assertThat(comment1).isNotEqualTo(comment2);
        assertThat(comment2).isNotEqualTo(comment3);
    }

    @Test
    @DisplayName("testCommentsOnSameFeedback - Multiple comments can reference same feedback")
    void testCommentsOnSameFeedback() {
        // Arrange
        User author2 = new User();
        author2.name = "Author 2";
        Comment comment2 = new Comment("Second comment", feedback, author2);

        // Act & Assert
        assertThat(comment.getFeedback()).isEqualTo(feedback);
        assertThat(comment2.getFeedback()).isEqualTo(feedback);
        assertThat(comment.getFeedback()).isEqualTo(comment2.getFeedback());
    }

    // ===== COMBINED STATE TESTS =====

    @Test
    @DisplayName("testComplexCommentState - Complex comment state with multiple properties")
    void testComplexCommentState() {
        // Arrange & Act
        comment.setText("Great feedback!");
        comment.setDeveloperResponse(true);
        comment.setUpvotes(15);

        User newAuthor = new User();
        newAuthor.name = "Developer";
        comment.setAuthor(newAuthor);

        // Assert
        assertThat(comment.getText()).isEqualTo("Great feedback!");
        assertThat(comment.isDeveloperResponse()).isTrue();
        assertThat(comment.getUpvotes()).isEqualTo(15);
        assertThat(comment.getAuthor().name).isEqualTo("Developer");
        assertThat(comment.getFeedback().getTitle()).isEqualTo("Test Feedback");
    }

    @Test
    @DisplayName("testCommentWithDifferentFeedbacks - Comment can be associated with different feedbacks")
    void testCommentWithDifferentFeedbacks() {
        // Arrange
        Feedback feedback2 = new Feedback("Another Feedback", "Different description");
        Feedback feedback3 = new Feedback("Third Feedback", "Third description");

        // Act
        assertThat(comment.getFeedback().getTitle()).isEqualTo("Test Feedback");

        comment.setFeedback(feedback2);
        assertThat(comment.getFeedback().getTitle()).isEqualTo("Another Feedback");

        comment.setFeedback(feedback3);
        assertThat(comment.getFeedback().getTitle()).isEqualTo("Third Feedback");
    }

    // ===== SPECIAL CHARACTER TESTS =====

    @Test
    @DisplayName("testCommentText_SpecialCharacters - Comment text can contain special characters")
    void testCommentText_SpecialCharacters() {
        // Act
        comment.setText("Great feedback! #awesome @user (very helpful)");

        // Assert
        assertThat(comment.getText()).isEqualTo("Great feedback! #awesome @user (very helpful)");
    }

    @Test
    @DisplayName("testCommentText_Multiline - Comment text can contain newlines")
    void testCommentText_Multiline() {
        // Act
        comment.setText("Line 1\nLine 2\nLine 3");

        // Assert
        assertThat(comment.getText()).contains("Line 1").contains("Line 2").contains("Line 3");
    }
}
