package com.agora.domain.feedback;

import com.agora.domain.feedback.model.entity.Comment;
import com.agora.domain.feedback.model.entity.Feedback;
import com.agora.domain.feedback.model.entity.FeedbackCategory;
import com.agora.domain.feedback.model.entity.FeedbackStatus;
import com.agora.domain.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.agora.domain.feedback.TestDataBuilder.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for TestDataBuilder fluent builders.
 * <p>
 * Tests verify that all builder classes correctly construct domain entities
 * with proper default values and customization support.
 * </p>
 */
@DisplayName("Test Data Builder Tests")
class TestDataBuilderTests {

    // ===== FEEDBACK BUILDER TESTS =====

    @Test
    @DisplayName("testFeedbackBuilder_DefaultValues - Creates feedback with default values")
    void testFeedbackBuilder_DefaultValues() {
        // Act
        Feedback feedback = feedback().build();

        // Assert
        assertThat(feedback.getTitle()).isEqualTo("Test Feedback");
        assertThat(feedback.getDescription()).isEqualTo("This is test feedback");
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.PENDING);
        assertThat(feedback.getUpvotes()).isZero();
        assertThat(feedback.getDownvotes()).isZero();
        assertThat(feedback.getComments()).isZero();
        assertThat(feedback.isArchived()).isFalse();
    }

    @Test
    @DisplayName("testFeedbackBuilder_CustomTitle - Title can be customized")
    void testFeedbackBuilder_CustomTitle() {
        // Act
        Feedback feedback = feedback()
            .title("Custom Title")
            .build();

        // Assert
        assertThat(feedback.getTitle()).isEqualTo("Custom Title");
    }

    @Test
    @DisplayName("testFeedbackBuilder_CustomDescription - Description can be customized")
    void testFeedbackBuilder_CustomDescription() {
        // Act
        Feedback feedback = feedback()
            .description("Custom description")
            .build();

        // Assert
        assertThat(feedback.getDescription()).isEqualTo("Custom description");
    }

    @Test
    @DisplayName("testFeedbackBuilder_CustomStatus - Status can be customized")
    void testFeedbackBuilder_CustomStatus() {
        // Act
        Feedback feedback = feedback()
            .status(FeedbackStatus.COMPLETED)
            .build();

        // Assert
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.COMPLETED);
    }

    @Test
    @DisplayName("testFeedbackBuilder_AllStatuses - All status values can be set")
    void testFeedbackBuilder_AllStatuses() {
        // Act & Assert
        assertThat(feedback().status(FeedbackStatus.PENDING).build().getStatus())
            .isEqualTo(FeedbackStatus.PENDING);
        assertThat(feedback().status(FeedbackStatus.ACKNOWLEDGED).build().getStatus())
            .isEqualTo(FeedbackStatus.ACKNOWLEDGED);
        assertThat(feedback().status(FeedbackStatus.IN_PROGRESS).build().getStatus())
            .isEqualTo(FeedbackStatus.IN_PROGRESS);
        assertThat(feedback().status(FeedbackStatus.COMPLETED).build().getStatus())
            .isEqualTo(FeedbackStatus.COMPLETED);
    }

    @Test
    @DisplayName("testFeedbackBuilder_WithCategory - Category can be set")
    void testFeedbackBuilder_WithCategory() {
        // Arrange
        FeedbackCategory category = category().name("Bug Report").build();

        // Act
        Feedback feedback = feedback()
            .category(category)
            .build();

        // Assert
        assertThat(feedback.getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("testFeedbackBuilder_WithAuthor - Author can be set")
    void testFeedbackBuilder_WithAuthor() {
        // Arrange
        User author = user().name("John Doe").build();

        // Act
        Feedback feedback = feedback()
            .author(author)
            .build();

        // Assert
        assertThat(feedback.getAuthor()).isEqualTo(author);
    }

    @Test
    @DisplayName("testFeedbackBuilder_WithSentiment - Sentiment can be set")
    void testFeedbackBuilder_WithSentiment() {
        // Act
        Feedback feedback = feedback()
            .sentiment("Positive")
            .build();

        // Assert
        assertThat(feedback.getSentiment()).isEqualTo("Positive");
    }

    @Test
    @DisplayName("testFeedbackBuilder_WithUpvotes - Upvotes can be set")
    void testFeedbackBuilder_WithUpvotes() {
        // Act
        Feedback feedback = feedback()
            .upvotes(10)
            .build();

        // Assert
        assertThat(feedback.getUpvotes()).isEqualTo(10);
    }

    @Test
    @DisplayName("testFeedbackBuilder_Archived - Archived flag can be set")
    void testFeedbackBuilder_Archived() {
        // Act
        Feedback feedback = feedback()
            .archived(true)
            .build();

        // Assert
        assertThat(feedback.isArchived()).isTrue();
    }

    @Test
    @DisplayName("testFeedbackBuilder_Fluent - Fluent chaining works")
    void testFeedbackBuilder_Fluent() {
        // Act
        Feedback feedback = feedback()
            .title("Feature Request")
            .description("A new feature")
            .status(FeedbackStatus.IN_PROGRESS)
            .sentiment("Neutral")
            .upvotes(5)
            .downvotes(2)
            .comments(3)
            .tags("important,urgent")
            .build();

        // Assert
        assertThat(feedback.getTitle()).isEqualTo("Feature Request");
        assertThat(feedback.getDescription()).isEqualTo("A new feature");
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.IN_PROGRESS);
        assertThat(feedback.getSentiment()).isEqualTo("Neutral");
        assertThat(feedback.getUpvotes()).isEqualTo(5);
        assertThat(feedback.getDownvotes()).isEqualTo(2);
        assertThat(feedback.getComments()).isEqualTo(3);
        assertThat(feedback.getTags()).isEqualTo("important,urgent");
    }

    // ===== CATEGORY BUILDER TESTS =====

    @Test
    @DisplayName("testCategoryBuilder_DefaultValues - Creates category with default values")
    void testCategoryBuilder_DefaultValues() {
        // Act
        FeedbackCategory category = category().build();

        // Assert
        assertThat(category.getId()).isNull();
        assertThat(category.getName()).isEqualTo("Test Category");
    }

    @Test
    @DisplayName("testCategoryBuilder_CustomName - Name can be customized")
    void testCategoryBuilder_CustomName() {
        // Act
        FeedbackCategory category = category()
            .name("Bug Report")
            .build();

        // Assert
        assertThat(category.getName()).isEqualTo("Bug Report");
    }

    @Test
    @DisplayName("testCategoryBuilder_WithId - ID can be set")
    void testCategoryBuilder_WithId() {
        // Act
        FeedbackCategory category = category()
            .id(123L)
            .name("Feature Request")
            .build();

        // Assert
        assertThat(category.getId()).isEqualTo(123L);
        assertThat(category.getName()).isEqualTo("Feature Request");
    }

    @Test
    @DisplayName("testCategoryBuilder_Fluent - Fluent chaining works")
    void testCategoryBuilder_Fluent() {
        // Act
        FeedbackCategory category = category()
            .id(456L)
            .name("Enhancement")
            .build();

        // Assert
        assertThat(category.getId()).isEqualTo(456L);
        assertThat(category.getName()).isEqualTo("Enhancement");
    }

    // ===== USER BUILDER TESTS =====

    @Test
    @DisplayName("testUserBuilder_DefaultValues - Creates user with default values")
    void testUserBuilder_DefaultValues() {
        // Act
        User user = user().build();

        // Assert
        assertThat(user.name).isEqualTo("Test User");
        assertThat(user.username).isNotBlank();
        assertThat(user.email).isNotBlank();
        assertThat(user.email).contains("@example.com");
    }

    @Test
    @DisplayName("testUserBuilder_CustomName - Name can be customized")
    void testUserBuilder_CustomName() {
        // Act
        User user = user()
            .name("Alice Smith")
            .build();

        // Assert
        assertThat(user.name).isEqualTo("Alice Smith");
    }

    @Test
    @DisplayName("testUserBuilder_CustomUsername - Username can be customized")
    void testUserBuilder_CustomUsername() {
        // Act
        User user = user()
            .username("alice_smith")
            .build();

        // Assert
        assertThat(user.username).isEqualTo("alice_smith");
    }

    @Test
    @DisplayName("testUserBuilder_CustomEmail - Email can be customized")
    void testUserBuilder_CustomEmail() {
        // Act
        User user = user()
            .email("alice@example.com")
            .build();

        // Assert
        assertThat(user.email).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("testUserBuilder_WithDiscordId - Discord ID can be set")
    void testUserBuilder_WithDiscordId() {
        // Act
        User user = user()
            .discordId(123456789L)
            .build();

        // Assert
        assertThat(user.discordId).isEqualTo(123456789L);
    }

    @Test
    @DisplayName("testUserBuilder_Fluent - Fluent chaining works")
    void testUserBuilder_Fluent() {
        // Act
        User user = user()
            .name("Bob Johnson")
            .username("bob_j")
            .email("bob@example.com")
            .discordId(987654321L)
            .discordUsername("BobJ#1234")
            .build();

        // Assert
        assertThat(user.name).isEqualTo("Bob Johnson");
        assertThat(user.username).isEqualTo("bob_j");
        assertThat(user.email).isEqualTo("bob@example.com");
        assertThat(user.discordId).isEqualTo(987654321L);
        assertThat(user.discordUsername).isEqualTo("BobJ#1234");
    }

    @Test
    @DisplayName("testUserBuilder_UniqueDefaults - Each user gets unique username and email")
    void testUserBuilder_UniqueDefaults() {
        // Act
        User user1 = user().build();
        User user2 = user().build();

        // Assert
        assertThat(user1.username).isNotEqualTo(user2.username);
        assertThat(user1.email).isNotEqualTo(user2.email);
    }

    // ===== COMMENT BUILDER TESTS =====

    @Test
    @DisplayName("testCommentBuilder_DefaultValues - Creates comment with default values")
    void testCommentBuilder_DefaultValues() {
        // Arrange
        Feedback feedback = feedback().build();
        User author = user().build();

        // Act
        Comment comment = comment()
            .feedback(feedback)
            .author(author)
            .build();

        // Assert
        assertThat(comment.getText()).isEqualTo("Test comment");
        assertThat(comment.getFeedback()).isEqualTo(feedback);
        assertThat(comment.getAuthor()).isEqualTo(author);
        assertThat(comment.isDeveloperResponse()).isFalse();
        assertThat(comment.getUpvotes()).isZero();
    }

    @Test
    @DisplayName("testCommentBuilder_CustomText - Text can be customized")
    void testCommentBuilder_CustomText() {
        // Arrange
        Feedback feedback = feedback().build();
        User author = user().build();

        // Act
        Comment comment = comment()
            .text("Great feedback!")
            .feedback(feedback)
            .author(author)
            .build();

        // Assert
        assertThat(comment.getText()).isEqualTo("Great feedback!");
    }

    @Test
    @DisplayName("testCommentBuilder_DeveloperResponse - Developer response flag can be set")
    void testCommentBuilder_DeveloperResponse() {
        // Arrange
        Feedback feedback = feedback().build();
        User author = user().build();

        // Act
        Comment comment = comment()
            .feedback(feedback)
            .author(author)
            .developerResponse(true)
            .build();

        // Assert
        assertThat(comment.isDeveloperResponse()).isTrue();
    }

    @Test
    @DisplayName("testCommentBuilder_WithUpvotes - Upvotes can be set")
    void testCommentBuilder_WithUpvotes() {
        // Arrange
        Feedback feedback = feedback().build();
        User author = user().build();

        // Act
        Comment comment = comment()
            .feedback(feedback)
            .author(author)
            .upvotes(10)
            .build();

        // Assert
        assertThat(comment.getUpvotes()).isEqualTo(10);
    }

    @Test
    @DisplayName("testCommentBuilder_Fluent - Fluent chaining works")
    void testCommentBuilder_Fluent() {
        // Arrange
        Feedback feedback = feedback().build();
        User author = user().build();

        // Act
        Comment comment = comment()
            .text("Excellent suggestion")
            .feedback(feedback)
            .author(author)
            .developerResponse(true)
            .upvotes(15)
            .build();

        // Assert
        assertThat(comment.getText()).isEqualTo("Excellent suggestion");
        assertThat(comment.isDeveloperResponse()).isTrue();
        assertThat(comment.getUpvotes()).isEqualTo(15);
    }

    @Test
    @DisplayName("testCommentBuilder_RequiresFeedback - Feedback is required")
    void testCommentBuilder_RequiresFeedback() {
        // Arrange
        User author = user().build();

        // Act & Assert
        assertThatThrownBy(() ->
            comment()
                .author(author)
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("feedback");
    }

    @Test
    @DisplayName("testCommentBuilder_RequiresAuthor - Author is required")
    void testCommentBuilder_RequiresAuthor() {
        // Arrange
        Feedback feedback = feedback().build();

        // Act & Assert
        assertThatThrownBy(() ->
            comment()
                .feedback(feedback)
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("author");
    }

    // ===== INTEGRATION TESTS =====

    @Test
    @DisplayName("testBuilders_CompleteScenario - All builders work together")
    void testBuilders_CompleteScenario() {
        // Act - Create all entities using builders
        FeedbackCategory category = category()
            .id(1L)
            .name("Bug Report")
            .build();

        User author = user()
            .name("Jane Doe")
            .username("jane_doe")
            .email("jane@example.com")
            .build();

        Feedback feedback = feedback()
            .title("Critical Bug Found")
            .description("Application crashes on startup")
            .status(FeedbackStatus.IN_PROGRESS)
            .category(category)
            .author(author)
            .sentiment("Negative")
            .upvotes(20)
            .comments(5)
            .build();

        User commenter = user()
            .name("Support Team")
            .username("support")
            .email("support@example.com")
            .build();

        Comment comment = comment()
            .text("We are investigating this issue")
            .feedback(feedback)
            .author(commenter)
            .developerResponse(true)
            .upvotes(10)
            .build();

        // Assert
        assertThat(feedback.getTitle()).isEqualTo("Critical Bug Found");
        assertThat(feedback.getCategory().getName()).isEqualTo("Bug Report");
        assertThat(feedback.getAuthor().name).isEqualTo("Jane Doe");
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.IN_PROGRESS);
        assertThat(comment.getText()).isEqualTo("We are investigating this issue");
        assertThat(comment.getFeedback()).isEqualTo(feedback);
        assertThat(comment.getAuthor().name).isEqualTo("Support Team");
    }

    @Test
    @DisplayName("testBuilders_MultipleInstances - Builders can create multiple instances")
    void testBuilders_MultipleInstances() {
        // Act
        Feedback feedback1 = feedback().title("First").build();
        Feedback feedback2 = feedback().title("Second").build();
        Feedback feedback3 = feedback().title("Third").build();

        // Assert
        assertThat(feedback1.getTitle()).isEqualTo("First");
        assertThat(feedback2.getTitle()).isEqualTo("Second");
        assertThat(feedback3.getTitle()).isEqualTo("Third");
        assertThat(feedback1).isNotEqualTo(feedback2);
        assertThat(feedback2).isNotEqualTo(feedback3);
    }
}
