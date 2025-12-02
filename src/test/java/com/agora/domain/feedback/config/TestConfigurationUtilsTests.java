package com.agora.domain.feedback.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for TestConfigurationUtils.
 * <p>
 * Validates that all test configuration constants and utilities are properly
 * defined and accessible for use in test suites.
 * </p>
 */
@DisplayName("Test Configuration Utils Tests")
class TestConfigurationUtilsTests {

    // ===== TIMEOUT CONFIGURATION TESTS =====

    @Test
    @DisplayName("testTimeoutConstants_AllPositive - All timeout constants are positive")
    void testTimeoutConstants_AllPositive() {
        // Assert
        assertThat(TestConfigurationUtils.TEST_TIMEOUT_SECONDS).isPositive();
        assertThat(TestConfigurationUtils.API_TEST_TIMEOUT_SECONDS).isPositive();
        assertThat(TestConfigurationUtils.INTEGRATION_TEST_TIMEOUT_SECONDS).isPositive();
    }

    @Test
    @DisplayName("testTimeoutConstants_Hierarchy - Integration timeout is greater than API timeout")
    void testTimeoutConstants_Hierarchy() {
        // Assert
        assertThat(TestConfigurationUtils.INTEGRATION_TEST_TIMEOUT_SECONDS)
            .isGreaterThan(TestConfigurationUtils.API_TEST_TIMEOUT_SECONDS);
    }

    // ===== DATABASE CONFIGURATION TESTS =====

    @Test
    @DisplayName("testDatabaseConstants_AllDefined - All database constants are defined")
    void testDatabaseConstants_AllDefined() {
        // Assert
        assertThat(TestConfigurationUtils.TEST_DATABASE_NAME).isNotBlank();
        assertThat(TestConfigurationUtils.TEST_DATABASE_USER).isNotBlank();
        assertThat(TestConfigurationUtils.TEST_DATABASE_PASSWORD).isNotBlank();
        assertThat(TestConfigurationUtils.TEST_DATABASE_HOST).isNotBlank();
        assertThat(TestConfigurationUtils.TEST_DATABASE_PORT).isPositive();
        assertThat(TestConfigurationUtils.TEST_DATABASE_DRIVER).isNotBlank();
    }

    @Test
    @DisplayName("testDatabaseUrl_FormattedCorrectly - Test database URL is properly formatted")
    void testDatabaseUrl_FormattedCorrectly() {
        // Act
        String url = TestConfigurationUtils.testDatabaseUrl();

        // Assert
        assertThat(url)
            .contains("postgresql://")
            .contains(TestConfigurationUtils.TEST_DATABASE_HOST)
            .contains(String.valueOf(TestConfigurationUtils.TEST_DATABASE_PORT))
            .contains(TestConfigurationUtils.TEST_DATABASE_NAME);
    }

    @Test
    @DisplayName("testDatabaseConnectionString_ContainsAllComponents - Connection string has all required parts")
    void testDatabaseConnectionString_ContainsAllComponents() {
        // Act
        String connectionString = TestConfigurationUtils.getDatabaseConnectionString();

        // Assert
        assertThat(connectionString)
            .contains("postgresql")
            .contains(TestConfigurationUtils.TEST_DATABASE_HOST)
            .contains(TestConfigurationUtils.TEST_DATABASE_NAME);
    }

    // ===== PAGINATION CONFIGURATION TESTS =====

    @Test
    @DisplayName("testPaginationConstants_ValidHierarchy - Pagination config has valid hierarchy")
    void testPaginationConstants_ValidHierarchy() {
        // Assert
        assertThat(TestConfigurationUtils.TEST_MAX_PAGE_SIZE)
            .isGreaterThan(TestConfigurationUtils.TEST_DEFAULT_PAGE_SIZE);
        assertThat(TestConfigurationUtils.TEST_DEFAULT_PAGE_SIZE)
            .isGreaterThanOrEqualTo(TestConfigurationUtils.TEST_MIN_PAGE_SIZE);
        assertThat(TestConfigurationUtils.TEST_MIN_PAGE_SIZE).isPositive();
    }

    // ===== REST API CONFIGURATION TESTS =====

    @Test
    @DisplayName("testRestApiConstants_AllDefined - All REST API constants are defined")
    void testRestApiConstants_AllDefined() {
        // Assert
        assertThat(TestConfigurationUtils.TEST_API_BASE_URL).isNotBlank();
        assertThat(TestConfigurationUtils.TEST_FEEDBACK_ENDPOINT).isNotBlank();
        assertThat(TestConfigurationUtils.TEST_AUTH_ENDPOINT).isNotBlank();
    }

    @Test
    @DisplayName("testFeedbackUrl_Formatted - Feedback URL is properly formatted")
    void testFeedbackUrl_Formatted() {
        // Act
        String url = TestConfigurationUtils.testFeedbackUrl();

        // Assert
        assertThat(url)
            .contains(TestConfigurationUtils.TEST_API_BASE_URL)
            .contains(TestConfigurationUtils.TEST_FEEDBACK_ENDPOINT);
    }

    @Test
    @DisplayName("testAuthUrl_Formatted - Auth URL is properly formatted")
    void testAuthUrl_Formatted() {
        // Act
        String url = TestConfigurationUtils.testAuthUrl();

        // Assert
        assertThat(url)
            .contains(TestConfigurationUtils.TEST_API_BASE_URL)
            .contains(TestConfigurationUtils.TEST_AUTH_ENDPOINT);
    }

    @Test
    @DisplayName("testBuildFeedbackUrl_ConstructsCorrectly - BuildFeedbackUrl appends path")
    void testBuildFeedbackUrl_ConstructsCorrectly() {
        // Act
        String url = TestConfigurationUtils.buildFeedbackUrl("/123");

        // Assert
        assertThat(url)
            .startsWith(TestConfigurationUtils.testFeedbackUrl())
            .endsWith("/123");
    }

    // ===== CONTENT TYPE TESTS =====

    @Test
    @DisplayName("testContentTypes_Defined - Content type constants are defined")
    void testContentTypes_Defined() {
        // Assert
        assertThat(TestConfigurationUtils.JSON_CONTENT_TYPE).isEqualTo("application/json");
        assertThat(TestConfigurationUtils.XML_CONTENT_TYPE).isEqualTo("application/xml");
    }

    // ===== HTTP STATUS CODES TESTS =====

    @Test
    @DisplayName("testHttpStatusCodes_SuccessCodes - Success status codes are correct")
    void testHttpStatusCodes_SuccessCodes() {
        // Assert
        assertThat(TestConfigurationUtils.HTTP_OK).isEqualTo(200);
        assertThat(TestConfigurationUtils.HTTP_CREATED).isEqualTo(201);
        assertThat(TestConfigurationUtils.HTTP_ACCEPTED).isEqualTo(202);
        assertThat(TestConfigurationUtils.HTTP_NO_CONTENT).isEqualTo(204);
    }

    @Test
    @DisplayName("testHttpStatusCodes_ClientErrorCodes - Client error status codes are correct")
    void testHttpStatusCodes_ClientErrorCodes() {
        // Assert
        assertThat(TestConfigurationUtils.HTTP_BAD_REQUEST).isEqualTo(400);
        assertThat(TestConfigurationUtils.HTTP_UNAUTHORIZED).isEqualTo(401);
        assertThat(TestConfigurationUtils.HTTP_FORBIDDEN).isEqualTo(403);
        assertThat(TestConfigurationUtils.HTTP_NOT_FOUND).isEqualTo(404);
        assertThat(TestConfigurationUtils.HTTP_CONFLICT).isEqualTo(409);
    }

    @Test
    @DisplayName("testHttpStatusCodes_ServerErrorCodes - Server error status codes are correct")
    void testHttpStatusCodes_ServerErrorCodes() {
        // Assert
        assertThat(TestConfigurationUtils.HTTP_INTERNAL_SERVER_ERROR).isEqualTo(500);
        assertThat(TestConfigurationUtils.HTTP_SERVICE_UNAVAILABLE).isEqualTo(503);
    }

    // ===== DATA CONSTRAINT TESTS =====

    @Test
    @DisplayName("testDataConstraints_AllPositive - All data constraint lengths are positive")
    void testDataConstraints_AllPositive() {
        // Assert
        assertThat(TestConfigurationUtils.MAX_FEEDBACK_TITLE_LENGTH).isPositive();
        assertThat(TestConfigurationUtils.MAX_FEEDBACK_DESCRIPTION_LENGTH).isPositive();
        assertThat(TestConfigurationUtils.MAX_COMMENT_TEXT_LENGTH).isPositive();
        assertThat(TestConfigurationUtils.MAX_CATEGORY_NAME_LENGTH).isPositive();
        assertThat(TestConfigurationUtils.MAX_USERNAME_LENGTH).isPositive();
        assertThat(TestConfigurationUtils.MAX_EMAIL_LENGTH).isPositive();
    }

    @Test
    @DisplayName("testDataConstraints_TitleSmallerThanDescription - Title constraint is smaller than description")
    void testDataConstraints_TitleSmallerThanDescription() {
        // Assert
        assertThat(TestConfigurationUtils.MAX_FEEDBACK_TITLE_LENGTH)
            .isLessThan(TestConfigurationUtils.MAX_FEEDBACK_DESCRIPTION_LENGTH);
    }

    // ===== FEATURE FLAG TESTS =====

    @Test
    @DisplayName("testFeatureFlags_AllEnabled - All feature flags are enabled for tests")
    void testFeatureFlags_AllEnabled() {
        // Assert
        assertThat(TestConfigurationUtils.ENABLE_VALIDATION_TESTS).isTrue();
        assertThat(TestConfigurationUtils.ENABLE_PAGINATION_TESTS).isTrue();
        assertThat(TestConfigurationUtils.ENABLE_ARCHIVE_TESTS).isTrue();
        assertThat(TestConfigurationUtils.ENABLE_COMMENT_TESTS).isTrue();
    }

    // ===== UTILITY METHOD TESTS =====

    @Test
    @DisplayName("testGetTestEnvironment_ReturnsTest - Test environment identifier is 'test'")
    void testGetTestEnvironment_ReturnsTest() {
        // Act
        String environment = TestConfigurationUtils.getTestEnvironment();

        // Assert
        assertThat(environment).isEqualTo("test");
    }

    @Test
    @DisplayName("testIsRunningInCI_ReturnsBoolean - CI detection returns boolean")
    void testIsRunningInCI_ReturnsBoolean() {
        // Act
        boolean isCi = TestConfigurationUtils.isRunningInCI();

        // Assert
        assertThat(isCi).isInstanceOf(Boolean.class);
    }

    @Test
    @DisplayName("testGetDefaultHeaders_ReturnsMap - Default headers returns populated map")
    void testGetDefaultHeaders_ReturnsMap() {
        // Act
        Map<String, String> headers = TestConfigurationUtils.getDefaultHeaders();

        // Assert
        assertThat(headers).isNotEmpty();
        assertThat(headers).containsKey("Content-Type");
        assertThat(headers).containsKey("Accept");
        assertThat(headers.get("Content-Type")).isEqualTo("application/json");
    }

    @Test
    @DisplayName("testFormatTestMessage_IncludesContext - Formatted message includes context")
    void testFormatTestMessage_IncludesContext() {
        // Act
        String message = TestConfigurationUtils.formatTestMessage("MyTest", "Something failed");

        // Assert
        assertThat(message)
            .contains("MyTest")
            .contains("Something failed")
            .startsWith("[");
    }

    @Test
    @DisplayName("testCreateUniqueTestId_WithPrefix_ContainsPrefix - Unique ID with prefix includes prefix")
    void testCreateUniqueTestId_WithPrefix_ContainsPrefix() {
        // Act
        String id = TestConfigurationUtils.createUniqueTestId("feedback");

        // Assert
        assertThat(id)
            .startsWith("feedback_")
            .contains("_");
    }

    @Test
    @DisplayName("testCreateUniqueTestId_WithoutPrefix_HasDefault - Unique ID without prefix has default")
    void testCreateUniqueTestId_WithoutPrefix_HasDefault() {
        // Act
        String id = TestConfigurationUtils.createUniqueTestId();

        // Assert
        assertThat(id)
            .startsWith("test_")
            .contains("_");
    }

    @Test
    @DisplayName("testCreateUniqueTestId_Uniqueness_DifferentCallsYieldDifferentIds - Multiple calls yield different IDs")
    void testCreateUniqueTestId_Uniqueness_DifferentCallsYieldDifferentIds() {
        // Act
        String id1 = TestConfigurationUtils.createUniqueTestId();
        String id2 = TestConfigurationUtils.createUniqueTestId();
        String id3 = TestConfigurationUtils.createUniqueTestId();

        // Assert
        assertThat(id1).isNotEqualTo(id2);
        assertThat(id2).isNotEqualTo(id3);
        assertThat(id1).isNotEqualTo(id3);
    }

    // ===== CONFIGURATION CONSISTENCY TESTS =====

    @Test
    @DisplayName("testConfiguration_Consistency_BaseUrlEndsWithoutSlash - Base URL ends without slash")
    void testConfiguration_Consistency_BaseUrlEndsWithoutSlash() {
        // Assert
        assertThat(TestConfigurationUtils.TEST_API_BASE_URL)
            .doesNotEndWith("/");
    }

    @Test
    @DisplayName("testConfiguration_Consistency_EndpointsStartWithSlash - Endpoints start with slash")
    void testConfiguration_Consistency_EndpointsStartWithSlash() {
        // Assert
        assertThat(TestConfigurationUtils.TEST_FEEDBACK_ENDPOINT)
            .startsWith("/");
        assertThat(TestConfigurationUtils.TEST_AUTH_ENDPOINT)
            .startsWith("/");
    }

    @Test
    @DisplayName("testConfiguration_AllStringsNotEmpty - All string constants are non-empty")
    void testConfiguration_AllStringsNotEmpty() {
        // Assert
        assertThat(TestConfigurationUtils.TEST_DATABASE_NAME).isNotBlank();
        assertThat(TestConfigurationUtils.TEST_DATABASE_USER).isNotBlank();
        assertThat(TestConfigurationUtils.TEST_DATABASE_PASSWORD).isNotBlank();
        assertThat(TestConfigurationUtils.TEST_DATABASE_HOST).isNotBlank();
        assertThat(TestConfigurationUtils.JSON_CONTENT_TYPE).isNotBlank();
    }
}
