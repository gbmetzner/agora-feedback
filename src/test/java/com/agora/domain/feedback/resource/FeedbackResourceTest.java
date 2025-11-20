package com.agora.domain.feedback.resource;

import com.agora.domain.feedback.model.entity.FeedbackStatus;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
class FeedbackResourceTest {

    @Test
    void testListAllFeedbacks() {
        given()
                .when().get("/api/feedbacks")
                .then()
                .statusCode(200);
    }

    @Test
    void testGetNonExistentFeedback() {
        given()
                .when().get("/api/feedbacks/99999")
                .then()
                .statusCode(404)
                .body("message", is("Feedback with id 99999 not found"));
    }

    @Test
    void testCreateFeedback() {
        String requestBody = """
                {
                    "title": "Test Feedback",
                    "description": "This is a test feedback content"
                }
                """;

        given()
                .contentType("application/json")
                .body(requestBody)
                .when().post("/api/feedbacks")
                .then()
                .statusCode(201)
                .body("title", is("Test Feedback"))
                .body("description", is("This is a test feedback content"))
                .body("status", is(FeedbackStatus.PENDING.name()))
                .body("createdAt", notNullValue())
                .body("archived", is(false));
    }

    @Test
    void testCreateFeedbackWithBlankTitle() {
        String requestBody = """
                {
                    "title": "",
                    "description": "This is a test feedback",
                    "status": "OPENED"
                }
                """;

        given()
                .contentType("application/json")
                .body(requestBody)
                .when().post("/api/feedbacks")
                .then()
                .statusCode(400)
                .body("message", is("Validation failed"))
                .body("errors.size()", greaterThan(0));
    }

    @Test
    void testCreateFeedbackWithShortDescription() {
        String requestBody = """
                {
                    "title": "Test",
                    "description": "Short",
                    "status": "OPENED"
                }
                """;

        given()
                .contentType("application/json")
                .body(requestBody)
                .when().post("/api/feedbacks")
                .then()
                .statusCode(400);
    }

    @Test
    void testCreateFeedbackWithInvalidStatus() {
        String requestBody = """
                {
                    "title": "Test Feedback",
                    "description": "This is a test feedback content",
                    "status": "INVALID"
                }
                """;

        given()
                .contentType("application/json")
                .body(requestBody)
                .when().post("/api/feedbacks")
                .then()
                .statusCode(400);
    }

    @Test
    void testUpdateFeedback() {
        // First create a feedback
        String createBody = """
                {
                    "title": "Original Title",
                    "description": "This is original feedback content here",
                    "status": "OPENED"
                }
                """;

        Integer feedbackId = given()
                .contentType("application/json")
                .body(createBody)
                .when().post("/api/feedbacks")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Now update it
        String updateBody = """
                {
                    "title": "Updated Title",
                    "description": "This is updated feedback content here",
                    "status": "CLOSED"
                }
                """;

        given()
                .contentType("application/json")
                .body(updateBody)
                .when().put("/api/feedbacks/" + feedbackId)
                .then()
                .statusCode(200)
                .body("title", is("Updated Title"))
                .body("description", is("This is updated feedback content here"))
                .body("status", is("CLOSED"));
    }

    @Test
    void testDeleteFeedback() {
        // First create a feedback
        String createBody = """
                {
                    "title": "To Delete",
                    "description": "This feedback will be deleted soon"
                }
                """;

        String feedbackId = given()
                .contentType("application/json")
                .body(createBody)
                .when().post("/api/feedbacks")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Delete it
        given()
                .when().delete("/api/feedbacks/" + feedbackId)
                .then()
                .statusCode(204);

        // Verify it's deleted
        given()
                .when().get("/api/feedbacks/" + feedbackId)
                .then()
                .statusCode(404);
    }

}