package com.agora.domain.feedback.fixture;

import com.agora.domain.feedback.model.entity.Comment;
import com.agora.domain.feedback.model.entity.Feedback;
import com.agora.domain.feedback.model.entity.FeedbackCategory;
import com.agora.domain.feedback.model.entity.FeedbackStatus;
import com.agora.domain.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for test fixtures.
 * <p>
 * Validates that all fixture factories create properly configured entities
 * with expected defaults and properties.
 * </p>
 */
@DisplayName("Fixture Tests")
class FixtureTests {

    // ===== USER FIXTURES TESTS =====

    @Test
    @DisplayName("testUserFixtures_StandardUser_HasValidProperties - Standard user has required properties")
    void testUserFixtures_StandardUser_HasValidProperties() {
        // Act
        User user = UserFixtures.standardUser();

        // Assert
        assertThat(user.name).isEqualTo("John Doe");
        assertThat(user.username).isNotBlank();
        assertThat(user.email).isNotBlank().contains("@example.com");
        assertThat(user.discordId).isEqualTo(111111111111111111L);
        assertThat(user.discordUsername).isEqualTo("JohnDoe#1234");
    }

    @Test
    @DisplayName("testUserFixtures_AdminUser_HasValidProperties - Admin user is properly configured")
    void testUserFixtures_AdminUser_HasValidProperties() {
        // Act
        User user = UserFixtures.adminUser();

        // Assert
        assertThat(user.name).isEqualTo("Admin User");
        assertThat(user.discordId).isEqualTo(222222222222222222L);
    }

    @Test
    @DisplayName("testUserFixtures_SupportUser_HasValidProperties - Support user is properly configured")
    void testUserFixtures_SupportUser_HasValidProperties() {
        // Act
        User user = UserFixtures.supportUser();

        // Assert
        assertThat(user.name).isEqualTo("Support Team");
        assertThat(user.discordId).isEqualTo(333333333333333333L);
    }

    @Test
    @DisplayName("testUserFixtures_DeveloperUser_HasValidProperties - Developer user is properly configured")
    void testUserFixtures_DeveloperUser_HasValidProperties() {
        // Act
        User user = UserFixtures.developerUser();

        // Assert
        assertThat(user.name).isEqualTo("Developer");
        assertThat(user.discordId).isEqualTo(444444444444444444L);
    }

    @Test
    @DisplayName("testUserFixtures_UniqueDefaults_EachUserHasUniqueCredentials - Unique users have unique usernames and emails")
    void testUserFixtures_UniqueDefaults_EachUserHasUniqueCredentials() {
        // Act
        User user1 = UserFixtures.standardUser();
        User user2 = UserFixtures.standardUser();

        // Assert
        assertThat(user1.username).isNotEqualTo(user2.username);
        assertThat(user1.email).isNotEqualTo(user2.email);
    }

    @Test
    @DisplayName("testUserFixtures_UserWithName_CustomNameApplied - Custom name fixture works")
    void testUserFixtures_UserWithName_CustomNameApplied() {
        // Act
        User user = UserFixtures.userWithName("Alice Smith");

        // Assert
        assertThat(user.name).isEqualTo("Alice Smith");
        assertThat(user.username).isNotBlank();
        assertThat(user.email).isNotBlank();
    }

    // ===== CATEGORY FIXTURES TESTS =====

    @Test
    @DisplayName("testCategoryFixtures_BugReport_CreatedSuccessfully - Bug report category created")
    void testCategoryFixtures_BugReport_CreatedSuccessfully() {
        // Act
        FeedbackCategory category = CategoryFixtures.bugReport();

        // Assert
        assertThat(category.getName()).isEqualTo("Bug Report");
    }

    @Test
    @DisplayName("testCategoryFixtures_FeatureRequest_CreatedSuccessfully - Feature request category created")
    void testCategoryFixtures_FeatureRequest_CreatedSuccessfully() {
        // Act
        FeedbackCategory category = CategoryFixtures.featureRequest();

        // Assert
        assertThat(category.getName()).isEqualTo("Feature Request");
    }

    @Test
    @DisplayName("testCategoryFixtures_AllCategories_HaveNames - All category fixtures have names")
    void testCategoryFixtures_AllCategories_HaveNames() {
        // Act & Assert
        assertThat(CategoryFixtures.bugReport().getName()).isNotBlank();
        assertThat(CategoryFixtures.featureRequest().getName()).isNotBlank();
        assertThat(CategoryFixtures.enhancement().getName()).isNotBlank();
        assertThat(CategoryFixtures.documentation().getName()).isNotBlank();
        assertThat(CategoryFixtures.performance().getName()).isNotBlank();
        assertThat(CategoryFixtures.usability().getName()).isNotBlank();
        assertThat(CategoryFixtures.security().getName()).isNotBlank();
        assertThat(CategoryFixtures.other().getName()).isNotBlank();
    }

    // ===== FEEDBACK FIXTURES TESTS =====

    @Test
    @DisplayName("testFeedbackFixtures_PendingBugReport_HasCorrectStatus - Pending bug report fixture has PENDING status")
    void testFeedbackFixtures_PendingBugReport_HasCorrectStatus() {
        // Act
        Feedback feedback = FeedbackFixtures.pendingBugReport();

        // Assert
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.PENDING);
        assertThat(feedback.getCategory().getName()).isEqualTo("Bug Report");
        assertThat(feedback.getAuthor()).isNotNull();
        assertThat(feedback.getSentiment()).isEqualTo("Negative");
    }

    @Test
    @DisplayName("testFeedbackFixtures_AcknowledgedFeatureRequest_HasCorrectStatus - Acknowledged feature request has correct status")
    void testFeedbackFixtures_AcknowledgedFeatureRequest_HasCorrectStatus() {
        // Act
        Feedback feedback = FeedbackFixtures.acknowledgedFeatureRequest();

        // Assert
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.ACKNOWLEDGED);
        assertThat(feedback.getCategory().getName()).isEqualTo("Feature Request");
    }

    @Test
    @DisplayName("testFeedbackFixtures_InProgressEnhancement_HasCorrectStatus - In-progress enhancement has correct status")
    void testFeedbackFixtures_InProgressEnhancement_HasCorrectStatus() {
        // Act
        Feedback feedback = FeedbackFixtures.inProgressEnhancement();

        // Assert
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("testFeedbackFixtures_CompletedFeedback_HasCorrectStatus - Completed feedback has correct status")
    void testFeedbackFixtures_CompletedFeedback_HasCorrectStatus() {
        // Act
        Feedback feedback = FeedbackFixtures.completedFeedback();

        // Assert
        assertThat(feedback.getStatus()).isEqualTo(FeedbackStatus.COMPLETED);
    }

    @Test
    @DisplayName("testFeedbackFixtures_ArchivedFeedback_IsArchived - Archived feedback fixture is marked archived")
    void testFeedbackFixtures_ArchivedFeedback_IsArchived() {
        // Act
        Feedback feedback = FeedbackFixtures.archivedFeedback();

        // Assert
        assertThat(feedback.isArchived()).isTrue();
    }

    @Test
    @DisplayName("testFeedbackFixtures_HighlyUpvotedFeedback_HasHighUpvotes - Highly upvoted feedback has many upvotes")
    void testFeedbackFixtures_HighlyUpvotedFeedback_HasHighUpvotes() {
        // Act
        Feedback feedback = FeedbackFixtures.highlyUpvotedFeedback();

        // Assert
        assertThat(feedback.getUpvotes()).isGreaterThan(100);
        assertThat(feedback.getDownvotes()).isLessThan(10);
    }

    @Test
    @DisplayName("testFeedbackFixtures_StateTransitionSequence_HasAllStatuses - State transition array has all statuses")
    void testFeedbackFixtures_StateTransitionSequence_HasAllStatuses() {
        // Act
        Feedback[] feedback = FeedbackFixtures.stateTransitionSequence();

        // Assert
        assertThat(feedback).hasSize(4);
        assertThat(feedback[0].getStatus()).isEqualTo(FeedbackStatus.PENDING);
        assertThat(feedback[1].getStatus()).isEqualTo(FeedbackStatus.ACKNOWLEDGED);
        assertThat(feedback[2].getStatus()).isEqualTo(FeedbackStatus.IN_PROGRESS);
        assertThat(feedback[3].getStatus()).isEqualTo(FeedbackStatus.COMPLETED);
    }

    // ===== COMMENT FIXTURES TESTS =====

    @Test
    @DisplayName("testCommentFixtures_UserComment_HasValidProperties - User comment is properly configured")
    void testCommentFixtures_UserComment_HasValidProperties() {
        // Arrange
        Feedback feedback = FeedbackFixtures.pendingBugReport();

        // Act
        Comment comment = CommentFixtures.userComment(feedback);

        // Assert
        assertThat(comment.getText()).isEqualTo("This is a great feature idea!");
        assertThat(comment.getFeedback()).isEqualTo(feedback);
        assertThat(comment.getAuthor()).isNotNull();
        assertThat(comment.isDeveloperResponse()).isFalse();
        assertThat(comment.getUpvotes()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("testCommentFixtures_SupportComment_IsDeveloperResponse - Support comment is marked as developer response")
    void testCommentFixtures_SupportComment_IsDeveloperResponse() {
        // Arrange
        Feedback feedback = FeedbackFixtures.pendingBugReport();

        // Act
        Comment comment = CommentFixtures.supportComment(feedback);

        // Assert
        assertThat(comment.isDeveloperResponse()).isTrue();
        assertThat(comment.getAuthor().name).isEqualTo("Support Team");
    }

    @Test
    @DisplayName("testCommentFixtures_DeveloperComment_IsDeveloperResponse - Developer comment is marked as developer response")
    void testCommentFixtures_DeveloperComment_IsDeveloperResponse() {
        // Arrange
        Feedback feedback = FeedbackFixtures.pendingBugReport();

        // Act
        Comment comment = CommentFixtures.developerComment(feedback);

        // Assert
        assertThat(comment.isDeveloperResponse()).isTrue();
        assertThat(comment.getAuthor().name).isEqualTo("Developer");
    }

    @Test
    @DisplayName("testCommentFixtures_CommentThread_HasMultipleComments - Comment thread has expected comments")
    void testCommentFixtures_CommentThread_HasMultipleComments() {
        // Arrange
        Feedback feedback = FeedbackFixtures.pendingBugReport();

        // Act
        Comment[] comments = CommentFixtures.commentThread(feedback);

        // Assert
        assertThat(comments).hasSize(5);
        assertThat(comments).allMatch(c -> c.getFeedback().equals(feedback));
    }

    // ===== SCENARIO FIXTURES TESTS =====

    @Test
    @DisplayName("testScenarioFixtures_BugReportScenario_HasAllComponents - Bug report scenario has feedback and comments")
    void testScenarioFixtures_BugReportScenario_HasAllComponents() {
        // Act
        ScenarioFixtures.BugReportScenario scenario = ScenarioFixtures.bugReportScenario();

        // Assert
        assertThat(scenario.feedback).isNotNull();
        assertThat(scenario.feedback.getCategory().getName()).isEqualTo("Bug Report");
        assertThat(scenario.reporter).isNotNull();
        assertThat(scenario.developer).isNotNull();
        assertThat(scenario.comments).hasSize(3);
        assertThat(scenario.comments[1].isDeveloperResponse()).isTrue();
    }

    @Test
    @DisplayName("testScenarioFixtures_FeatureRequestScenario_HasAllComponents - Feature request scenario has feedback and comments")
    void testScenarioFixtures_FeatureRequestScenario_HasAllComponents() {
        // Act
        ScenarioFixtures.FeatureRequestScenario scenario = ScenarioFixtures.featureRequestScenario();

        // Assert
        assertThat(scenario.feedback).isNotNull();
        assertThat(scenario.category.getName()).isEqualTo("Feature Request");
        assertThat(scenario.requester).isNotNull();
        assertThat(scenario.comments).hasSize(4);
    }

    @Test
    @DisplayName("testScenarioFixtures_ComplexScenario_HasMixedContent - Complex scenario has diverse feedback")
    void testScenarioFixtures_ComplexScenario_HasMixedContent() {
        // Act
        ScenarioFixtures.ComplexFeedbackScenario scenario = ScenarioFixtures.complexScenario();

        // Assert
        assertThat(scenario.feedback).hasSize(9);
        assertThat(scenario.users).hasSize(4);
        assertThat(scenario.categories).hasSize(3);

        // Verify 3 bugs, 3 features, 3 enhancements
        long bugCount = java.util.Arrays.stream(scenario.feedback)
            .filter(f -> f.getCategory().getName().equals("Bug Report"))
            .count();
        assertThat(bugCount).isEqualTo(3);
    }

    @Test
    @DisplayName("testScenarioFixtures_PaginationScenario_HasExpectedItemCount - Pagination scenario has 25 items")
    void testScenarioFixtures_PaginationScenario_HasExpectedItemCount() {
        // Act
        ScenarioFixtures.PaginationScenario scenario = ScenarioFixtures.paginationScenario();

        // Assert
        assertThat(scenario.feedbackItems).hasSize(25);
        assertThat(scenario.totalCount).isEqualTo(25);
        assertThat(scenario.feedbackItems).allMatch(f -> f.getAuthor() != null);
    }

    @Test
    @DisplayName("testScenarioFixtures_ArchiveScenario_HasActiveAndArchived - Archive scenario has both active and archived items")
    void testScenarioFixtures_ArchiveScenario_HasActiveAndArchived() {
        // Act
        ScenarioFixtures.ArchiveScenario scenario = ScenarioFixtures.archiveScenario();

        // Assert
        assertThat(scenario.activeFeedback.isArchived()).isFalse();
        assertThat(scenario.archivedFeedback.isArchived()).isTrue();
        assertThat(scenario.activeFeedback.getAuthor()).isEqualTo(scenario.user);
        assertThat(scenario.archivedFeedback.getAuthor()).isEqualTo(scenario.user);
    }

    // ===== FIXTURE FACTORY METHOD TESTS =====

    @Test
    @DisplayName("testScenarioFactories_ReturnValidScenarios - All scenario factory methods return valid scenarios")
    void testScenarioFactories_ReturnValidScenarios() {
        // Act & Assert
        assertThat(ScenarioFixtures.bugReportScenario()).isNotNull();
        assertThat(ScenarioFixtures.featureRequestScenario()).isNotNull();
        assertThat(ScenarioFixtures.complexScenario()).isNotNull();
        assertThat(ScenarioFixtures.paginationScenario()).isNotNull();
        assertThat(ScenarioFixtures.archiveScenario()).isNotNull();
    }

    @Test
    @DisplayName("testFixtures_Reusability_CanCreateMultipleInstances - Fixtures can be reused to create multiple instances")
    void testFixtures_Reusability_CanCreateMultipleInstances() {
        // Act
        Feedback feedback1 = FeedbackFixtures.pendingBugReport();
        Feedback feedback2 = FeedbackFixtures.pendingBugReport();
        Feedback feedback3 = FeedbackFixtures.pendingBugReport();

        // Assert
        assertThat(feedback1).isNotSameAs(feedback2);
        assertThat(feedback2).isNotSameAs(feedback3);
        assertThat(feedback1.getTitle()).isEqualTo(feedback2.getTitle());
    }

    @Test
    @DisplayName("testFixtures_Consistency_SameFixtureSameProperties - Same fixture type has consistent properties")
    void testFixtures_Consistency_SameFixtureSameProperties() {
        // Act
        Feedback feedback1 = FeedbackFixtures.pendingBugReport();
        Feedback feedback2 = FeedbackFixtures.pendingBugReport();

        // Assert
        assertThat(feedback1.getTitle()).isEqualTo(feedback2.getTitle());
        assertThat(feedback1.getDescription()).isEqualTo(feedback2.getDescription());
        assertThat(feedback1.getStatus()).isEqualTo(feedback2.getStatus());
        assertThat(feedback1.getSentiment()).isEqualTo(feedback2.getSentiment());
    }
}
