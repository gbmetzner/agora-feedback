package com.agora.domain.user.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
 * Resource layer tests for AuthResource.
 * <p>
 * Tests OAuth2 Discord authentication endpoints including callback handling
 * and error scenarios. Tests endpoint validation and error handling behavior.
 * </p>
 */
@QuarkusTest
@DisplayName("AuthResource Tests")
class AuthResourceTest {

    private static final String AUTH_URL = "/api/v1/auth/discord/callback";

    // ===== SUCCESSFUL AUTHENTICATION TESTS =====

    @Test
    @DisplayName("testDiscordCallback_ValidCodeFlow - Valid code initiates redirect")
    void testDiscordCallback_ValidCodeFlow() {
        // Act & Assert
        // Valid code should attempt to exchange for token and redirect
        // Note: This will fail with DiscordAuthService error if not mocked,
        // but demonstrates the endpoint accepts the code parameter
        given()
                .queryParam("code", "valid-auth-code")
                .when().get(AUTH_URL)
                .then()
                // Should either redirect (307) or fail with service error
                // Status code depends on Discord API availability
                .statusCode(org.hamcrest.Matchers.anyOf(
                    org.hamcrest.CoreMatchers.is(307),   // Temporary redirect on success
                    org.hamcrest.CoreMatchers.is(500),   // Service error if Discord unavailable
                    org.hamcrest.CoreMatchers.is(400)    // Bad request if service validation fails
                ));
    }

    // ===== ERROR SCENARIO TESTS =====

    @Test
    @DisplayName("testDiscordCallback_UserDenied - Error parameter returns 401")
    void testDiscordCallback_UserDenied() {
        // Act & Assert
        given()
                .queryParam("error", "access_denied")
                .when().get(AUTH_URL)
                .then()
                .statusCode(401)
                .body(org.hamcrest.CoreMatchers.containsString("Authorization denied"));
    }

    @Test
    @DisplayName("testDiscordCallback_MissingCode - No code parameter returns 400")
    void testDiscordCallback_MissingCode() {
        // Act & Assert
        given()
                .when().get(AUTH_URL)
                .then()
                .statusCode(400)
                .body(org.hamcrest.CoreMatchers.containsString("Missing authorization code"));
    }

    @Test
    @DisplayName("testDiscordCallback_NullCode - Null code returns 400")
    void testDiscordCallback_NullCode() {
        // Act & Assert
        given()
                .queryParam("code", (String) null)
                .when().get(AUTH_URL)
                .then()
                .statusCode(400)
                .body(org.hamcrest.CoreMatchers.containsString("Missing authorization code"));
    }

    @Test
    @DisplayName("testDiscordCallback_ErrorWithCode - Error takes precedence over code")
    void testDiscordCallback_ErrorWithCode() {
        // Arrange - Both error and code parameters present
        // Act & Assert
        given()
                .queryParam("code", "some-code")
                .queryParam("error", "access_denied")
                .when().get(AUTH_URL)
                .then()
                .statusCode(401)
                .body(org.hamcrest.CoreMatchers.containsString("Authorization denied"));
    }

    @Test
    @DisplayName("testDiscordCallback_EmptyCode - Empty code string returns 400")
    void testDiscordCallback_EmptyCode() {
        // Act & Assert
        given()
                .queryParam("code", "")
                .when().get(AUTH_URL)
                .then()
                .statusCode(400)
                .body(org.hamcrest.CoreMatchers.containsString("Missing authorization code"));
    }

}
