package com.agora.domain.feedback.integration;

import com.agora.domain.feedback.application.dto.CreateFeedbackCommand;
import com.agora.domain.feedback.model.dto.CreateCommentRequest;
import com.agora.domain.feedback.model.dto.FeedbackResponse;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Simplified end-to-end workflow integration tests.
 * <p>
 * Tests key feedback lifecycle workflows combining multiple endpoints.
 * Focuses on successful workflows to demonstrate end-to-end functionality.
 * </p>
 */
@QuarkusTest
@DisplayName("Feedback Workflow Integration Tests")
class FeedbackWorkflowIntegrationTest {

    private static final String FEEDBACK_URL = "/api/v1/feedback";

    @Test
    @DisplayName("testCompleteWorkflow_CreateCommentArchive - Full feedback lifecycle")
    void testCompleteWorkflow_CreateCommentArchive() {
        // Step 1: Create feedback
        CreateFeedbackCommand command = new CreateFeedbackCommand(
            "Test Feedback Title",
            "Test feedback description content",
            null, null, null, null
        );

        FeedbackResponse created = given()
            .contentType("application/json")
            .body(command)
            .when().post(FEEDBACK_URL)
            .then()
            .statusCode(201)
            .extract().body().as(FeedbackResponse.class);

        assertThat(created).isNotNull();
        assertThat(created.title()).isEqualTo("Test Feedback Title");

        String feedbackId = created.id();

        // Step 2: Add a comment
        CreateCommentRequest commentRequest = new CreateCommentRequest("This is a comment");

        given()
            .contentType("application/json")
            .body(commentRequest)
            .when().put(FEEDBACK_URL + "/{id}/comments", feedbackId)
            .then()
            .statusCode(201);

        // Step 3: Archive the feedback
        FeedbackResponse archived = given()
            .when().post(FEEDBACK_URL + "/{id}/archive", feedbackId)
            .then()
            .statusCode(200)
            .extract().body().as(FeedbackResponse.class);

        assertThat(archived.archived()).isTrue();

        // Step 4: Reopen it
        FeedbackResponse reopened = given()
            .when().post(FEEDBACK_URL + "/{id}/reopen", feedbackId)
            .then()
            .statusCode(200)
            .extract().body().as(FeedbackResponse.class);

        assertThat(reopened.archived()).isFalse();
    }

@Test
    @DisplayName("testDeleteWorkflow_CreateAndDelete - Delete feedback")
    void testDeleteWorkflow_CreateAndDelete() {
        // Step 1: Create feedback
        CreateFeedbackCommand command = new CreateFeedbackCommand(
            "To Be Deleted",
            "This will be deleted",
            null, null, null, null
        );

        FeedbackResponse created = given()
            .contentType("application/json")
            .body(command)
            .when().post(FEEDBACK_URL)
            .then()
            .statusCode(201)
            .extract().body().as(FeedbackResponse.class);

        String feedbackId = created.id();

        // Step 2: Delete it
        given()
            .when().delete(FEEDBACK_URL + "/{id}", feedbackId)
            .then()
            .statusCode(204);

        // Step 3: Verify it's gone
        given()
            .when().get(FEEDBACK_URL + "/{id}", feedbackId)
            .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("testCommentWorkflow_AddMultipleComments - Multiple comments")
    void testCommentWorkflow_AddMultipleComments() {
        // Step 1: Create feedback
        CreateFeedbackCommand command = new CreateFeedbackCommand(
            "Discussion Topic",
            "Let's discuss this",
            null, null, null, null
        );

        FeedbackResponse feedback = given()
            .contentType("application/json")
            .body(command)
            .when().post(FEEDBACK_URL)
            .then()
            .statusCode(201)
            .extract().body().as(FeedbackResponse.class);

        String feedbackId = feedback.id();

        // Step 2: Add multiple comments
        for (int i = 1; i <= 3; i++) {
            CreateCommentRequest commentRequest = new CreateCommentRequest(
                "Comment number " + i
            );

            given()
                .contentType("application/json")
                .body(commentRequest)
                .when().put(FEEDBACK_URL + "/{id}/comments", feedbackId)
                .then()
                .statusCode(201);
        }

        // Step 3: Retrieve and verify comments exist
        String comments = given()
            .when().get(FEEDBACK_URL + "/{id}/comments", feedbackId)
            .then()
            .statusCode(200)
            .extract().body().asString();

        assertThat(comments).isNotEmpty();
    }

    @Test
    @DisplayName("testBulkWorkflow_CreateMultiple - Create multiple feedbacks")
    void testBulkWorkflow_CreateMultiple() {
        // Create 5 feedback items
        String[] ids = new String[5];

        for (int i = 0; i < 5; i++) {
            CreateFeedbackCommand command = new CreateFeedbackCommand(
                "Item " + (i + 1),
                "Description " + (i + 1),
                null, null, null, null
            );

            FeedbackResponse response = given()
                .contentType("application/json")
                .body(command)
                .when().post(FEEDBACK_URL)
                .then()
                .statusCode(201)
                .extract().body().as(FeedbackResponse.class);

            ids[i] = response.id();
        }

        // Verify all were created
        for (String id : ids) {
            given()
                .when().get(FEEDBACK_URL + "/{id}", id)
                .then()
                .statusCode(200);
        }

        assertThat(ids).allMatch(id -> id != null && !id.isEmpty());
    }
}
