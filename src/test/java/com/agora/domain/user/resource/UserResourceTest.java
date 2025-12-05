package com.agora.domain.user.resource;

import com.agora.domain.user.model.Role;
import com.agora.domain.user.model.User;
import com.agora.domain.user.model.dto.LeaderboardEntry;
import com.agora.domain.user.model.dto.PaginatedLeaderboardResponse;
import com.agora.domain.user.model.repository.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests for UserResource leaderboard endpoints
 */
@QuarkusTest
@DisplayName("User Resource Tests")
class UserResourceTest {

    private static final String USERS_URL = "/api/v1/users";

    @Inject
    UserRepository userRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        // Clear existing users and create test users with varying reputation scores
        userRepository.deleteAll();

        long timestamp = System.nanoTime();

        // Create test users with different reputation scores
        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.name = "Test User " + i;
            user.username = "testuser_" + i + "_" + timestamp;
            user.email = "testuser" + i + "_" + timestamp + "@test.com";
            user.discordId = 100000L + i;
            user.discordUsername = "testuser_discord_" + i;
            user.reputationScore = i * 100; // 100, 200, 300, 400, 500
            user.role = Role.USER;
            userRepository.persist(user);
        }
    }

    @Test
    @DisplayName("testGetLeaderboard_Success - Retrieve paginated leaderboard")
    void testGetLeaderboard_Success() {
        var response = given()
                .queryParam("page", 1)
                .queryParam("pageSize", 10)
                .when().get(USERS_URL + "/leaderboard")
                .then()
                .statusCode(200)
                .extract().body().as(PaginatedLeaderboardResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.entries()).isNotNull();
        assertThat(response.entries().size()).isGreaterThan(0);
        assertThat(response.currentPage()).isEqualTo(1);
        assertThat(response.totalUsers()).isGreaterThanOrEqualTo(5);
    }

    @Test
    @DisplayName("testGetLeaderboard_RankedByReputation - Users ordered by reputation descending")
    void testGetLeaderboard_RankedByReputation() {
        var response = given()
                .queryParam("page", 1)
                .queryParam("pageSize", 10)
                .when().get(USERS_URL + "/leaderboard")
                .then()
                .statusCode(200)
                .extract().body().as(PaginatedLeaderboardResponse.class);

        // Verify entries are sorted by reputation score descending
        for (int i = 0; i < response.entries().size() - 1; i++) {
            int current = response.entries().get(i).reputationScore();
            int next = response.entries().get(i + 1).reputationScore();
            assertThat(current).isGreaterThanOrEqualTo(next);
        }
    }

    @Test
    @DisplayName("testGetLeaderboard_WithPagination - Test pagination parameters")
    void testGetLeaderboard_WithPagination() {
        var response = given()
                .queryParam("page", 1)
                .queryParam("pageSize", 2)
                .when().get(USERS_URL + "/leaderboard")
                .then()
                .statusCode(200)
                .extract().body().as(PaginatedLeaderboardResponse.class);

        assertThat(response.entries().size()).isLessThanOrEqualTo(2);
        assertThat(response.pageSize()).isEqualTo(2);
        assertThat(response.currentPage()).isEqualTo(1);
    }

    @Test
    @DisplayName("testGetLeaderboard_PageSizeCapped - Verify page size capped at 100")
    void testGetLeaderboard_PageSizeCapped() {
        var response = given()
                .queryParam("page", 1)
                .queryParam("pageSize", 500)
                .when().get(USERS_URL + "/leaderboard")
                .then()
                .statusCode(200)
                .extract().body().as(PaginatedLeaderboardResponse.class);

        assertThat(response.pageSize()).isLessThanOrEqualTo(100);
    }

    @Test
    @DisplayName("testGetLeaderboard_DefaultPage - Verify defaults to page 1")
    void testGetLeaderboard_DefaultPage() {
        var response = given()
                .when().get(USERS_URL + "/leaderboard")
                .then()
                .statusCode(200)
                .extract().body().as(PaginatedLeaderboardResponse.class);

        assertThat(response.currentPage()).isGreaterThan(0);
    }

    @Test
    @DisplayName("testGetTopUsers_Success - Retrieve top users")
    void testGetTopUsers_Success() {
        var response = given()
                .queryParam("limit", 3)
                .when().get(USERS_URL + "/leaderboard/top")
                .then()
                .statusCode(200)
                .extract().body().as(LeaderboardEntry[].class);

        assertThat(response).isNotNull();
        assertThat(response.length).isLessThanOrEqualTo(3);
    }

    @Test
    @DisplayName("testGetTopUsers_RankedByReputation - Top users ordered by reputation")
    void testGetTopUsers_RankedByReputation() {
        var response = given()
                .queryParam("limit", 5)
                .when().get(USERS_URL + "/leaderboard/top")
                .then()
                .statusCode(200)
                .extract().body().as(LeaderboardEntry[].class);

        // Verify top users are sorted by reputation descending
        for (int i = 0; i < response.length - 1; i++) {
            int current = response[i].reputationScore();
            int next = response[i + 1].reputationScore();
            assertThat(current).isGreaterThanOrEqualTo(next);
        }
    }

    @Test
    @DisplayName("testGetTopUsers_LimitCapped - Verify limit capped at 100")
    void testGetTopUsers_LimitCapped() {
        var response = given()
                .queryParam("limit", 500)
                .when().get(USERS_URL + "/leaderboard/top")
                .then()
                .statusCode(200)
                .extract().body().as(LeaderboardEntry[].class);

        assertThat(response.length).isLessThanOrEqualTo(100);
    }

    @Test
    @DisplayName("testGetTopUsers_DefaultLimit - Verify defaults to 10")
    void testGetTopUsers_DefaultLimit() {
        var response = given()
                .when().get(USERS_URL + "/leaderboard/top")
                .then()
                .statusCode(200)
                .extract().body().as(LeaderboardEntry[].class);

        assertThat(response.length).isLessThanOrEqualTo(10);
    }

    @Test
    @DisplayName("testLeaderboardEntry_HasRequiredFields - Verify entry structure")
    void testLeaderboardEntry_HasRequiredFields() {
        var response = given()
                .queryParam("limit", 1)
                .when().get(USERS_URL + "/leaderboard/top")
                .then()
                .statusCode(200)
                .extract().body().as(LeaderboardEntry[].class);

        assertThat(response.length).isGreaterThan(0);
        LeaderboardEntry entry = response[0];
        assertThat(entry.userId()).isNotNull();
        assertThat(entry.username()).isNotNull();
        assertThat(entry.displayName()).isNotNull();
        assertThat(entry.reputationScore()).isNotNull();
    }
}
