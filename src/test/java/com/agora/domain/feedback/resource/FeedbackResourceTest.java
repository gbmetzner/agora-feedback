package com.agora.domain.feedback.resource;

import com.agora.domain.feedback.application.dto.CreateFeedbackCommand;
import com.agora.domain.feedback.application.dto.UpdateFeedbackCommand;
import com.agora.domain.feedback.common.IdHelper;
import com.agora.domain.feedback.model.dto.CommentResponse;
import com.agora.domain.feedback.model.dto.CreateCommentRequest;
import com.agora.domain.feedback.model.dto.FeedbackResponse;
import com.agora.domain.feedback.model.dto.PaginatedFeedbackResponse;
import com.agora.domain.feedback.model.entity.FeedbackStatus;
import com.agora.domain.user.infrastructure.security.JwtService;
import com.agora.domain.user.model.repository.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

/**
 * Resource layer tests for FeedbackResource.
 * <p>
 * Tests HTTP endpoints for feedback submission, retrieval, management,
 * comments, and pagination. Uses REST Assured for HTTP client calls
 * and AssertJ for fluent assertions.
 * </p>
 */
@QuarkusTest
@DisplayName("FeedbackResource Tests")
class FeedbackResourceTest {

    private static final String FEEDBACK_URL = "/api/v1/feedback";
    private static final String INVALID_FEEDBACK_ID = IdHelper.toString(117457749108987300L);
    private static final String AUTHORIZATION_HEADER = "Authorization";
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


    // ===== LIST AND RETRIEVE TESTS =====

    @Test
    @DisplayName("testListAllFeedbacks - Retrieve paginated feedbacks")
    void testListAllFeedbacks() {
        var response = given()
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .get()
                .then()
                .statusCode(200)
                .extract().body().as(PaginatedFeedbackResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.currentPage()).isPositive();
        assertThat(response.pageSize()).isGreaterThan(0);
        assertThat(response.totalItems()).isGreaterThanOrEqualTo(0);
        assertThat(response.items()).isNotNull();
    }

    @Test
    @DisplayName("testGetNonExistentFeedback - Returns 404 for invalid ID")
    void testGetNonExistentFeedback() {
        // Use a numerically valid but non-existent ID (large TSID value)
        given()
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .get(INVALID_FEEDBACK_ID)
                .then()
                .statusCode(404);
    }

    // ===== CREATE FEEDBACK TESTS =====

    @Test
    @DisplayName("testCreateFeedback_Success - Valid feedback creation")
    void testCreateFeedback_Success() {
        var command = CreateFeedbackCommand.builder()
                .title("Test Feedback Title")
                .description("This is a comprehensive test feedback content for testing purposes")
                .build();

        var response = given()
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(command)
                .when().post()
                .then()
                .statusCode(201)
                .extract().body().as(FeedbackResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.title()).isEqualTo(command.title());
        assertThat(response.description()).isEqualTo(command.description());
        assertThat(response.status()).isEqualTo(FeedbackStatus.PENDING);
        assertThat(response.upvotes()).isZero();
        assertThat(response.comments()).isZero();
        assertThat(response.archived()).isFalse();
    }

    @Test
    @DisplayName("testCreateFeedbackWithBlankTitle - Validation error for empty title")
    void testCreateFeedbackWithBlankTitle() {
        var command = CreateFeedbackCommand.builder()
                .title("")
                .description("This is a test feedback with blank title")
                .build();

        given()
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(command)
                .when().post()
                .then()
                .statusCode(400)
                .body("message", org.hamcrest.CoreMatchers.is("Validation failed"));
    }

    @Test
    @DisplayName("testCreateFeedbackWithShortDescription - Validation error for short description")
    void testCreateFeedbackWithShortDescription() {
        var command = CreateFeedbackCommand.builder()
                .title("Valid Title")
                .description("Short")
                .build();

        given()
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(command)
                .when().post()
                .then()
                .statusCode(400);
    }

    // ===== UPDATE FEEDBACK TESTS =====

    @Test
    @DisplayName("testUpdateFeedback_Success - Update existing feedback")
    void testUpdateFeedback_Success() {
        // Create feedback
        var createCommand = CreateFeedbackCommand.builder()
                .title("Original Title")
                .description("This is original feedback content that will be updated")
                .build();

        var createdFeedback = given()
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(createCommand)
                .when().post()
                .then()
                .statusCode(201)
                .extract().body().as(FeedbackResponse.class);

        // Update feedback - Requires authentication
        var updateCommand = new UpdateFeedbackCommand(
                "Updated Title",
                "This is the updated feedback content here",
                FeedbackStatus.IN_PROGRESS,
                null,
                null,
                null,
                null
        );

        given()
                .accept("application/json")
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(updateCommand)
                .when()
                .patch(createdFeedback.id())
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("testUpdateFeedback_NotFound - Returns 403 for unauthenticated request")
    void testUpdateFeedback_NotFound() {
        var updateCommand = new UpdateFeedbackCommand(
                "Updated Title",
                "Updated description here",
                FeedbackStatus.COMPLETED,
                null,
                null,
                null,
                null
        );

        given()
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(updateCommand)
                .when()
                .patch( INVALID_FEEDBACK_ID)
                .then()
                .statusCode(404);
    }

    // ===== DELETE FEEDBACK TESTS =====

    @Test
    @DisplayName("testDeleteFeedback_Success - Delete existing feedback")
    void testDeleteFeedback_Success() {
        // Create feedback
        var command = CreateFeedbackCommand.builder()
                .title("To Delete")
                .description("This feedback will be deleted soon to verify deletion")
                .build();

        var createdFeedback = given()
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(command)
                .when().post()
                .then()
                .statusCode(201)
                .extract().body().as(FeedbackResponse.class);

        // Delete it
        given()
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .delete(createdFeedback.id())
                .then()
                .statusCode(204);

        // Verify it's deleted
        given()
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .get( createdFeedback.id())
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("testDeleteFeedback_NotFound - Returns 404 for non-existent feedback")
    void testDeleteFeedback_NotFound() {
        given()
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .delete(INVALID_FEEDBACK_ID)
                .then()
                .statusCode(404);
    }

    // ===== ARCHIVE FEEDBACK TESTS =====

    @Test
    @DisplayName("testArchiveFeedback_Success - Archive existing feedback")
    void testArchiveFeedback_Success() {
        // Create feedback
        var command = CreateFeedbackCommand.builder()
                .title("To Archive")
                .description("This feedback will be archived to test the archive functionality")
                .build();

        var createdFeedback = given()
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(command)
                .when().post()
                .then()
                .statusCode(201)
                .extract().body().as(FeedbackResponse.class);

        // Archive it
        var response = given()
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .post( createdFeedback.id() + "/archive")
                .then()
                .statusCode(200)
                .extract().body().as(FeedbackResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.archived()).isTrue();
    }

    @Test
    @DisplayName("testArchiveFeedback_NotFound - Returns 404 for non-existent feedback")
    void testArchiveFeedback_NotFound() {
        given()
                .accept("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .contentType("application/json")
                .when().post(INVALID_FEEDBACK_ID+"/archive")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("testArchiveFeedback_VerifyArchivedFlag - Verify archived flag is set")
    void testArchiveFeedback_VerifyArchivedFlag() {
        // Create feedback
        var command = CreateFeedbackCommand.builder()
                .title("Verify Archive Flag")
                .description("Testing that archived flag is properly set when archiving feedback")
                .build();

        var createdFeedback = given()
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(command)
                .when().post()
                .then()
                .statusCode(201)
                .extract().body().as(FeedbackResponse.class);

        assertThat(createdFeedback.archived()).isFalse();

        var archivedFeedback = given()
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .accept("application/json")
                .contentType("application/json")
                .post(createdFeedback.id() + "/archive")
                .then()
                .statusCode(200)
                .extract().body().as(FeedbackResponse.class);

        assertThat(archivedFeedback.archived()).isTrue();

        // Verify by fetching again
        var fetchedFeedback = given()
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .get( createdFeedback.id())
                .then()
                .statusCode(200)
                .extract().body().as(FeedbackResponse.class);

        assertThat(fetchedFeedback.archived()).isTrue();
    }

    // ===== REOPEN FEEDBACK TESTS =====

    @Test
    @DisplayName("testReopenFeedback_Success - Reopen archived feedback")
    void testReopenFeedback_Success() {
        // Create and archive feedback
        var command = CreateFeedbackCommand.builder()
                .title("To Reopen")
                .description("This feedback will be archived then reopened for testing")
                .build();

        var createdFeedback = given()
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(command)
                .when().post()
                .then()
                .statusCode(201)
                .extract().body().as(FeedbackResponse.class);

        // Archive it first
        given()
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .post( createdFeedback.id() + "/archive")
                .then()
                .statusCode(200);

        // Reopen it
        var response = given()
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .post( createdFeedback.id() + "/reopen")
                .then()
                .statusCode(200)
                .extract().body().as(FeedbackResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.archived()).isFalse();
    }

    @Test
    @DisplayName("testReopenFeedback_NotFound - Returns 404 for non-existent feedback")
    void testReopenFeedback_NotFound() {
        given()
                .when()
                .accept("application/json")
                .contentType("application/json")
                .post(FEEDBACK_URL + "/" + INVALID_FEEDBACK_ID + "/reopen")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("testReopenFeedback_VerifyStatus - Verify status is changed to PENDING")
    void testReopenFeedback_VerifyStatus() {
        // Create feedback
        var command = CreateFeedbackCommand.builder()
                .title("Verify Reopen Status")
                .description("Testing that reopen changes status back to PENDING when reopening")
                .build();

        var createdFeedback = given()
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(command)
                .when().post()
                .then()
                .statusCode(201)
                .extract().body().as(FeedbackResponse.class);

        // Archive it first
        given()
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .accept("application/json")
                .contentType("application/json")
                .post( createdFeedback.id() + "/archive")
                .then()
                .statusCode(200);

        // Reopen and verify
        var reopenedFeedback = given()
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .accept("application/json")
                .contentType("application/json")
                .post( createdFeedback.id() + "/reopen")
                .then()
                .statusCode(200)
                .extract().body().as(FeedbackResponse.class);

        assertThat(reopenedFeedback.archived()).isFalse();
        assertThat(reopenedFeedback.status()).isEqualTo(FeedbackStatus.PENDING);
    }

    // ===== COMMENT TESTS =====

    @Test
    @DisplayName("testGetComments_Success - Retrieve comments for feedback")
    void testGetComments_Success() {
        // Create feedback
        var command = CreateFeedbackCommand.builder()
                .title("Feedback for Comments")
                .description("This feedback will have comments added to test comment retrieval")
                .build();

        var createdFeedback = given()
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(command)
                .when().post()
                .then()
                .statusCode(201)
                .extract().body().as(FeedbackResponse.class);

        // Retrieve comments (should be empty initially)
        var response = given()
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .get(createdFeedback.id() + "/comments")
                .then()
                .statusCode(200)
                .extract().body().as(CommentResponse[].class);

        assertThat(response).isNotNull();
        assertThat(response).isEmpty();
    }

    @Test
    @DisplayName("testGetComments_EmptyList - Returns empty array for feedback with no comments")
    void testGetComments_EmptyList() {
        // Create feedback without comments
        var command = CreateFeedbackCommand.builder()
                .title("No Comments Feedback")
                .description("This feedback intentionally has no comments for testing empty list")
                .build();

        var createdFeedback = given()
                .contentType("application/json")
                .body(command)
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .post()
                .then()
                .statusCode(201)
                .extract().body().as(FeedbackResponse.class);

        var comments = given()
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .get( createdFeedback.id() + "/comments")
                .then()
                .statusCode(200)
                .extract().body().as(CommentResponse[].class);

        assertThat(comments).isEmpty();
    }

    @Test
    @DisplayName("testGetComments_FeedbackNotFound - Returns 404 for invalid feedback ID")
    void testGetComments_FeedbackNotFound() {
        given()
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .get( INVALID_FEEDBACK_ID + "/comments")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("testAddComment_Success - Add comment to feedback")
    void testAddComment_Success() {
        // Create feedback
        var command = CreateFeedbackCommand.builder()
                .title("Feedback for Comment")
                .description("This feedback will have a comment added to test comment creation")
                .build();

        var createdFeedback = given()
                .contentType("application/json")
                .body(command)
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .post()
                .then()
                .statusCode(201)
                .extract().body().as(FeedbackResponse.class);

        // Add comment
        var commentRequest = new CreateCommentRequest("Great feedback! This is very helpful and well-written.");

        var response = given()
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(commentRequest)
                .when().put(createdFeedback.id() + "/comments")
                .then()
                .statusCode(201)
                .extract().body().as(CommentResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.text()).isEqualTo(commentRequest.text());
        assertThat(response.id()).isNotNull();
    }

    @Test
    @DisplayName("testAddComment_VerifyCommentCount - Verify feedback comment count incremented")
    void testAddComment_VerifyCommentCount() {
        // Create feedback
        var command = CreateFeedbackCommand.builder()
                .title("Feedback for Count Test")
                .description("This feedback will have a comment added to verify count increment")
                .build();

        var createdFeedback = given()
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(command)
                .when().post()
                .then()
                .statusCode(201)
                .extract().body().as(FeedbackResponse.class);

        assertThat(createdFeedback.comments()).isZero();

        // Add comment
        var commentRequest = new CreateCommentRequest("This is a test comment to verify count increment.");

        given()
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(commentRequest)
                .when().put(createdFeedback.id() + "/comments")
                .then()
                .statusCode(201);

        // Verify count increased
        var updatedFeedback = given()
                .when()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .get(createdFeedback.id())
                .then()
                .statusCode(200)
                .extract().body().as(FeedbackResponse.class);

        assertThat(updatedFeedback.comments()).isGreaterThan(0);
    }

    @Test
    @DisplayName("testAddComment_FeedbackNotFound - Returns 404 for invalid feedback ID")
    void testAddComment_FeedbackNotFound() {
        var commentRequest = new CreateCommentRequest("This comment is for a non-existent feedback.");

        given()
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(commentRequest)
                .when().put(INVALID_FEEDBACK_ID + "/comments")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("testAddComment_BlankText - Validation error for empty comment text")
    void testAddComment_BlankText() {
        // Create feedback
        var command = CreateFeedbackCommand.builder()
                .title("Feedback for Blank Comment")
                .description("This feedback will attempt to have an empty comment added")
                .build();

        var createdFeedback = given()
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(command)
                .when().post()
                .then()
                .statusCode(201)
                .extract().body().as(FeedbackResponse.class);

        var emptyCommentRequest = new CreateCommentRequest("");

        given()
                .contentType("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .body(emptyCommentRequest)
                .when().put(createdFeedback.id() + "/comments")
                .then()
                .statusCode(400);
    }

    // ===== PAGINATION TESTS =====

    @Test
    @DisplayName("testListAll_WithPagination - Test page and pageSize parameters")
    void testListAll_WithPagination() {
        var response = given()
.queryParam("page", 1)
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .queryParam("pageSize", 5)
                .when().get()
                .then()
                .statusCode(200)
                .extract().body().as(PaginatedFeedbackResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.currentPage()).isEqualTo(1);
        assertThat(response.pageSize()).isLessThanOrEqualTo(5);
    }

    @Test
    @DisplayName("testListAll_SortByOldest - Test sortBy=oldest parameter")
    void testListAll_SortByOldest() {
        var response = given()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
.queryParam("sortBy", "oldest")
                .when().get()
                .then()
                .statusCode(200)
                .extract().body().as(PaginatedFeedbackResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.items()).isNotNull();
    }

    @Test
    @DisplayName("testListAll_SortByNewest - Test sortBy=newest (default)")
    void testListAll_SortByNewest() {
        var response = given()
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
.queryParam("sortBy", "newest")
                .when().get()
                .then()
                .statusCode(200)
                .extract().body().as(PaginatedFeedbackResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.items()).isNotNull();
    }

    @Test
    @DisplayName("testListAll_InvalidPageNumber - Verify defaults to page 1")
    void testListAll_InvalidPageNumber() {
        var response = given()
.accept("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .contentType("application/json")
                .queryParam("page", -1)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().body().as(PaginatedFeedbackResponse.class);

        assertThat(response).isNotNull();
        // Should either default to 1 or handle gracefully
        assertThat(response.currentPage()).isGreaterThan(0);
    }

    @Test
    @DisplayName("testListAll_ExcessivePageSize - Verify capped at 100")
    void testListAll_ExcessivePageSize() {
        var response = given()
.accept("application/json")
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .contentType("application/json")
                .queryParam("pageSize", 500)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().body().as(PaginatedFeedbackResponse.class);

        assertThat(response).isNotNull();
        // Should cap at maximum size (100 per spec)
        assertThat(response.pageSize()).isLessThanOrEqualTo(100);
    }

}
