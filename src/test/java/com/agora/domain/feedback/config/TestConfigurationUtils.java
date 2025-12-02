package com.agora.domain.feedback.config;

/**
 * Utilities for test configuration and environment management.
 * <p>
 * Provides constants and methods for configuring test behavior,
 * managing test timeouts, database configuration, and other test-specific settings.
 * </p>
 */
public class TestConfigurationUtils {

    // ===== TIMEOUT CONFIGURATION =====

    public static final long TEST_TIMEOUT_SECONDS = 30;
    public static final long API_TEST_TIMEOUT_SECONDS = 5;
    public static final long INTEGRATION_TEST_TIMEOUT_SECONDS = 60;

    // ===== DATABASE CONFIGURATION =====

    public static final String TEST_DATABASE_NAME = "feedback_test";
    public static final String TEST_DATABASE_USER = "testuser";
    public static final String TEST_DATABASE_PASSWORD = "testpassword";
    public static final String TEST_DATABASE_HOST = "localhost";
    public static final int TEST_DATABASE_PORT = 5432;
    public static final String TEST_DATABASE_DRIVER = "org.postgresql.Driver";

    public static String testDatabaseUrl() {
        return String.format(
            "jdbc:postgresql://%s:%d/%s",
            TEST_DATABASE_HOST,
            TEST_DATABASE_PORT,
            TEST_DATABASE_NAME
        );
    }

    // ===== PAGINATION CONFIGURATION =====

    public static final int TEST_MAX_PAGE_SIZE = 20;
    public static final int TEST_DEFAULT_PAGE_SIZE = 10;
    public static final int TEST_MIN_PAGE_SIZE = 1;

    // ===== REST API CONFIGURATION =====

    public static final String TEST_API_BASE_URL = "http://localhost:8080";
    public static final String TEST_FEEDBACK_ENDPOINT = "/v1/api/feedback";
    public static final String TEST_AUTH_ENDPOINT = "/v1/api/auth";

    public static String testFeedbackUrl() {
        return TEST_API_BASE_URL + TEST_FEEDBACK_ENDPOINT;
    }

    public static String testAuthUrl() {
        return TEST_API_BASE_URL + TEST_AUTH_ENDPOINT;
    }

    // ===== CONTENT TYPE CONFIGURATION =====

    public static final String JSON_CONTENT_TYPE = "application/json";
    public static final String XML_CONTENT_TYPE = "application/xml";

    // ===== HTTP STATUS CODES =====

    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_ACCEPTED = 202;
    public static final int HTTP_NO_CONTENT = 204;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_CONFLICT = 409;
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;
    public static final int HTTP_SERVICE_UNAVAILABLE = 503;

    // ===== TEST DATA CONSTRAINTS =====

    public static final int MAX_FEEDBACK_TITLE_LENGTH = 255;
    public static final int MAX_FEEDBACK_DESCRIPTION_LENGTH = 2000;
    public static final int MAX_COMMENT_TEXT_LENGTH = 1000;
    public static final int MAX_CATEGORY_NAME_LENGTH = 100;
    public static final int MAX_USERNAME_LENGTH = 50;
    public static final int MAX_EMAIL_LENGTH = 255;

    // ===== FEATURE FLAGS FOR TESTS =====

    public static final boolean ENABLE_VALIDATION_TESTS = true;
    public static final boolean ENABLE_PAGINATION_TESTS = true;
    public static final boolean ENABLE_ARCHIVE_TESTS = true;
    public static final boolean ENABLE_COMMENT_TESTS = true;

    // ===== UTILITY METHODS =====

    /**
     * Get the test environment name.
     *
     * @return Test environment identifier
     */
    public static String getTestEnvironment() {
        return "test";
    }

    /**
     * Check if running in continuous integration.
     *
     * @return true if running in CI environment
     */
    public static boolean isRunningInCI() {
        return System.getenv("CI") != null ||
               System.getenv("GITHUB_ACTIONS") != null ||
               System.getenv("GITLAB_CI") != null ||
               System.getenv("JENKINS_URL") != null;
    }

    /**
     * Get database connection string.
     *
     * @return Full JDBC connection string
     */
    public static String getDatabaseConnectionString() {
        return String.format(
            "postgresql://%s:%d/%s",
            TEST_DATABASE_HOST,
            TEST_DATABASE_PORT,
            TEST_DATABASE_NAME
        );
    }

    /**
     * Build a complete feedback endpoint URL with path.
     *
     * @param path The path component (e.g., "/123" or "/comments")
     * @return Complete endpoint URL
     */
    public static String buildFeedbackUrl(String path) {
        return testFeedbackUrl() + path;
    }

    /**
     * Get default test headers for API requests.
     *
     * @return Map of default headers
     */
    public static java.util.Map<String, String> getDefaultHeaders() {
        java.util.Map<String, String> headers = new java.util.HashMap<>();
        headers.put("Content-Type", JSON_CONTENT_TYPE);
        headers.put("Accept", JSON_CONTENT_TYPE);
        return headers;
    }

    /**
     * Format a test assertion message with context.
     *
     * @param context The context/test name
     * @param message The assertion message
     * @return Formatted message
     */
    public static String formatTestMessage(String context, String message) {
        return String.format("[%s] %s", context, message);
    }

    /**
     * Create a unique test identifier for isolation.
     *
     * @param prefix Optional prefix for the identifier
     * @return Unique identifier string
     */
    public static String createUniqueTestId(String prefix) {
        return (prefix != null ? prefix : "test") + "_" + System.nanoTime();
    }

    /**
     * Create a unique test identifier.
     *
     * @return Unique identifier string
     */
    public static String createUniqueTestId() {
        return createUniqueTestId(null);
    }
}
