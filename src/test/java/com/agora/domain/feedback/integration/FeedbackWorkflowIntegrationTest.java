package com.agora.domain.feedback.integration;

import com.agora.domain.feedback.application.dto.CreateFeedbackCommand;
import com.agora.domain.feedback.model.dto.CreateCommentRequest;
import com.agora.domain.feedback.model.dto.FeedbackResponse;
import com.agora.domain.user.infrastructure.security.JwtService;
import com.agora.domain.user.model.repository.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
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


    public static final String AUTHORIZATION_HEADER = "Authorization";


    @Inject
    UserRepository userRepository;
    private String AUTHORIZATION_TOKEN = "Bearer ";
    private final JwtService jwtService = new JwtService();

    @BeforeEach
    public void beforeEach() {
        RestAssured.basePath = "/api/v1/feedback";
        var user = userRepository.find("id",117457749108987388L).firstResult();
        AUTHORIZATION_TOKEN += jwtService.generateToken(user);
    }

    @Test
    @DisplayName("testCompleteWorkflow_CreateCommentArchive - Full feedback lifecycle")
    void testCompleteWorkflow_CreateCommentArchive() {
        // Step 1: Create feedback
        var command = CreateFeedbackCommand.builder()
                .title("Test Feedback Title")
                .description("Test feedback description content")
                .build();

        var created = given()
            .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
            .body(command)
            .when().post()
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
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
            .body(commentRequest)
            .when().put( "/{id}/comments", feedbackId)
            .then()
            .statusCode(201);

        // Step 3: Archive the feedback
        FeedbackResponse archived = given()
            .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .post( "/{id}/archive", feedbackId)
            .then()
            .statusCode(200)
            .extract().body().as(FeedbackResponse.class);

        assertThat(archived.archived()).isTrue();

        // Step 4: Reopen it
        FeedbackResponse reopened = given()
            .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .post( "/{id}/reopen", feedbackId)
            .then()
            .statusCode(200)
            .extract().body().as(FeedbackResponse.class);

        assertThat(reopened.archived()).isFalse();
    }

@Test
    @DisplayName("testDeleteWorkflow_CreateAndDelete - Delete feedback")
    void testDeleteWorkflow_CreateAndDelete() {
        // Step 1: Create feedback
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
            .title("To Be Deleted")
            .description("This will be deleted")
            .build();

        FeedbackResponse created = given()
            .contentType("application/json")
            .body(command)
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
            .when().post()
            .then()
            .statusCode(201)
            .extract().body().as(FeedbackResponse.class);

        String feedbackId = created.id();

        // Step 2: Delete it
        given()
            .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .delete( "/{id}", feedbackId)
            .then()
            .statusCode(204);

        // Step 3: Verify it's gone
        given()
            .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .get( "/{id}", feedbackId)
            .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("testCommentWorkflow_AddMultipleComments - Multiple comments")
    void testCommentWorkflow_AddMultipleComments() {
        // Step 1: Create feedback
        CreateFeedbackCommand command = CreateFeedbackCommand.builder()
            .title("Discussion Topic")
            .description("Let's discuss this")
            .build();

        FeedbackResponse feedback = given()
            .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
            .body(command)
            .when().post()
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
                    .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(commentRequest)
                .when().put( "/{id}/comments", feedbackId)
                .then()
                .statusCode(201);
        }

        // Step 3: Retrieve and verify comments exist
        String comments = given()
            .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .get( "/{id}/comments", feedbackId)
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
            CreateFeedbackCommand command = CreateFeedbackCommand.builder()
                .title("Item " + (i + 1))
                .description("Description " + (i + 1))
                .build();

            FeedbackResponse response = given()
                .contentType("application/json")
                    .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(command)
                .when().post()
                .then()
                .statusCode(201)
                .extract().body().as(FeedbackResponse.class);

            ids[i] = response.id();
        }

        // Verify all were created
        for (String id : ids) {
            given()
                    .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .when().get( "/{id}", id)
                .then()
                .statusCode(200);
        }

        assertThat(ids).allMatch(id -> id != null && !id.isEmpty());
    }
}
