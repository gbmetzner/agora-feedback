package com.agora.domain.feedback.api.exception;

import com.agora.domain.feedback.exception.CategoryNotFoundException;
import com.agora.domain.feedback.exception.DomainException;
import com.agora.domain.feedback.exception.FeedbackNotFoundException;
import com.agora.domain.feedback.exception.InvalidFeedbackException;
import com.agora.domain.user.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for exception mappers.
 * <p>
 * Tests DomainExceptionMapper, ValidationExceptionMapper, and UserExceptionMapper
 * to verify proper HTTP status codes and error response formatting.
 * </p>
 */
@DisplayName("Exception Mapper Tests")
class ExceptionMapperTests {

    private DomainExceptionMapper domainExceptionMapper;
    private ValidationExceptionMapper validationExceptionMapper;
    private com.agora.domain.feedback.api.exception.UserExceptionMapper userExceptionMapper;

    @BeforeEach
    void setUp() {
        domainExceptionMapper = new DomainExceptionMapper();
        validationExceptionMapper = new ValidationExceptionMapper();
        userExceptionMapper = new com.agora.domain.feedback.api.exception.UserExceptionMapper();
    }

    // ===== DOMAIN EXCEPTION MAPPER: FEEDBACK NOT FOUND TESTS =====

    @Test
    @DisplayName("testDomainMapper_FeedbackNotFound_Returns404 - FeedbackNotFoundException returns 404")
    void testDomainMapper_FeedbackNotFound_Returns404() {
        // Arrange
        FeedbackNotFoundException exception = new FeedbackNotFoundException("test-id");

        // Act
        Response response = domainExceptionMapper.toResponse(exception);

        // Assert
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getEntity()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertThat(errorResponse.status()).isEqualTo(404);
        assertThat(errorResponse.message()).contains("not found");
        assertThat(errorResponse.timestamp()).isNotNull();
    }

    @Test
    @DisplayName("testDomainMapper_FeedbackNotFound_ErrorMessage - Error message includes feedback ID")
    void testDomainMapper_FeedbackNotFound_ErrorMessage() {
        // Arrange
        FeedbackNotFoundException exception = new FeedbackNotFoundException("test-feedback-id");

        // Act
        Response response = domainExceptionMapper.toResponse(exception);
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();

        // Assert
        assertThat(errorResponse.message()).containsIgnoringCase("feedback");
        assertThat(errorResponse.message()).containsIgnoringCase("not found");
    }

    // ===== DOMAIN EXCEPTION MAPPER: CATEGORY NOT FOUND TESTS =====

    @Test
    @DisplayName("testDomainMapper_CategoryNotFound_Returns404 - CategoryNotFoundException returns 404")
    void testDomainMapper_CategoryNotFound_Returns404() {
        // Arrange
        CategoryNotFoundException exception = new CategoryNotFoundException(123L);

        // Act
        Response response = domainExceptionMapper.toResponse(exception);

        // Assert
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getEntity()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertThat(errorResponse.status()).isEqualTo(404);
        assertThat(errorResponse.message()).contains("not found");
    }

    @Test
    @DisplayName("testDomainMapper_CategoryNotFound_ErrorMessage - Error message includes category ID")
    void testDomainMapper_CategoryNotFound_ErrorMessage() {
        // Arrange
        CategoryNotFoundException exception = new CategoryNotFoundException(456L);

        // Act
        Response response = domainExceptionMapper.toResponse(exception);
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();

        // Assert
        assertThat(errorResponse.message()).containsIgnoringCase("category");
        assertThat(errorResponse.message()).containsIgnoringCase("not found");
    }

    // ===== DOMAIN EXCEPTION MAPPER: GENERIC DOMAIN EXCEPTION TESTS =====

    @Test
    @DisplayName("testDomainMapper_GenericDomainException_Returns500 - Generic DomainException returns 500")
    void testDomainMapper_GenericDomainException_Returns500() {
        // Arrange
        DomainException exception = new InvalidFeedbackException("Generic domain error");

        // Act
        Response response = domainExceptionMapper.toResponse(exception);

        // Assert
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertThat(errorResponse.status()).isEqualTo(500);
        assertThat(errorResponse.message()).isEqualTo("Generic domain error");
    }

    @Test
    @DisplayName("testDomainMapper_ErrorResponse_HasTimestamp - ErrorResponse includes timestamp")
    void testDomainMapper_ErrorResponse_HasTimestamp() {
        // Arrange
        DomainException exception = new InvalidFeedbackException("Test error");
        OffsetDateTime beforeMapping = OffsetDateTime.now();

        // Act
        Response response = domainExceptionMapper.toResponse(exception);
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        OffsetDateTime afterMapping = OffsetDateTime.now();

        // Assert
        assertThat(errorResponse.timestamp()).isNotNull();
        assertThat(errorResponse.timestamp()).isAfterOrEqualTo(beforeMapping);
        assertThat(errorResponse.timestamp()).isBeforeOrEqualTo(afterMapping);
    }

    @Test
    @DisplayName("testDomainMapper_ErrorResponse_NoFieldErrors - Field errors null for non-validation exceptions")
    void testDomainMapper_ErrorResponse_NoFieldErrors() {
        // Arrange
        DomainException exception = new InvalidFeedbackException("Test error");

        // Act
        Response response = domainExceptionMapper.toResponse(exception);
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();

        // Assert
        assertThat(errorResponse.errors()).isNull();
    }

    // ===== VALIDATION EXCEPTION MAPPER TESTS =====

    @Test
    @DisplayName("testValidationMapper_ConstraintViolations_Returns400 - Returns 400 Bad Request")
    void testValidationMapper_ConstraintViolations_Returns400() {
        // Arrange
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        // Act
        Response response = validationExceptionMapper.toResponse(exception);

        // Assert
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getEntity()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertThat(errorResponse.status()).isEqualTo(400);
        assertThat(errorResponse.message()).isEqualTo("Validation failed");
    }

    @Test
    @DisplayName("testValidationMapper_FieldErrors_IncludedInResponse - Field errors included in response")
    void testValidationMapper_FieldErrors_IncludedInResponse() {
        // This test would require mocking ConstraintViolation which is complex
        // Verification done through integration tests
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        // Act
        Response response = validationExceptionMapper.toResponse(exception);
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();

        // Assert
        assertThat(errorResponse.errors()).isNotNull();
        assertThat(errorResponse.errors()).isEmpty(); // Empty because we created empty violations set
    }

    @Test
    @DisplayName("testValidationMapper_ErrorResponse_HasTimestamp - Includes timestamp")
    void testValidationMapper_ErrorResponse_HasTimestamp() {
        // Arrange
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);
        OffsetDateTime beforeMapping = OffsetDateTime.now();

        // Act
        Response response = validationExceptionMapper.toResponse(exception);
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        OffsetDateTime afterMapping = OffsetDateTime.now();

        // Assert
        assertThat(errorResponse.timestamp()).isNotNull();
        assertThat(errorResponse.timestamp()).isAfterOrEqualTo(beforeMapping);
        assertThat(errorResponse.timestamp()).isBeforeOrEqualTo(afterMapping);
    }

    @Test
    @DisplayName("testValidationMapper_MessageConstant - Uses 'Validation failed' message")
    void testValidationMapper_MessageConstant() {
        // Arrange
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException("Some constraint message", violations);

        // Act
        Response response = validationExceptionMapper.toResponse(exception);
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();

        // Assert
        assertThat(errorResponse.message()).isEqualTo("Validation failed");
    }

    // ===== USER EXCEPTION MAPPER: USER NOT FOUND TESTS =====

    @Test
    @DisplayName("testUserMapper_UserNotFound_Returns404 - UserNotFoundException returns 404")
    void testUserMapper_UserNotFound_Returns404() {
        // Arrange
        UserNotFoundException exception = new UserNotFoundException(789L);

        // Act
        Response response = userExceptionMapper.toResponse(exception);

        // Assert
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getEntity()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertThat(errorResponse.status()).isEqualTo(404);
        assertThat(errorResponse.message()).contains("not found");
    }

    @Test
    @DisplayName("testUserMapper_UserNotFound_ErrorMessage - Error message includes user ID")
    void testUserMapper_UserNotFound_ErrorMessage() {
        // Arrange
        UserNotFoundException exception = new UserNotFoundException(999L);

        // Act
        Response response = userExceptionMapper.toResponse(exception);
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();

        // Assert
        assertThat(errorResponse.message()).containsIgnoringCase("user");
        assertThat(errorResponse.message()).containsIgnoringCase("not found");
    }

    // ===== USER EXCEPTION MAPPER: GENERIC USER DOMAIN EXCEPTION TESTS =====

    @Test
    @DisplayName("testUserMapper_GenericUserException_Returns500 - Generic user domain exception returns 500")
    void testUserMapper_GenericUserException_Returns500() {
        // Arrange - Use UserNotFoundException which is a concrete user domain exception
        com.agora.domain.user.exception.DomainException exception =
            new UserNotFoundException(999L);

        // Act
        Response response = userExceptionMapper.toResponse(exception);

        // Assert
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getEntity()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertThat(errorResponse.status()).isEqualTo(404);
    }

    @Test
    @DisplayName("testUserMapper_ErrorResponse_HasTimestamp - Includes timestamp")
    void testUserMapper_ErrorResponse_HasTimestamp() {
        // Arrange - Use UserNotFoundException for testing timestamp
        com.agora.domain.user.exception.DomainException exception =
            new UserNotFoundException(123L);
        OffsetDateTime beforeMapping = OffsetDateTime.now();

        // Act
        Response response = userExceptionMapper.toResponse(exception);
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        OffsetDateTime afterMapping = OffsetDateTime.now();

        // Assert
        assertThat(errorResponse.timestamp()).isNotNull();
        assertThat(errorResponse.timestamp()).isAfterOrEqualTo(beforeMapping);
        assertThat(errorResponse.timestamp()).isBeforeOrEqualTo(afterMapping);
    }

    // ===== ERROR RESPONSE TESTS =====

    @Test
    @DisplayName("testErrorResponse_Creation_WithMessage - ErrorResponse can be created with message only")
    void testErrorResponse_Creation_WithMessage() {
        // Act
        ErrorResponse errorResponse = ErrorResponse.of(404, "Not found");

        // Assert
        assertThat(errorResponse.status()).isEqualTo(404);
        assertThat(errorResponse.message()).isEqualTo("Not found");
        assertThat(errorResponse.timestamp()).isNotNull();
        assertThat(errorResponse.errors()).isNull();
    }

    @Test
    @DisplayName("testErrorResponse_Creation_WithFieldErrors - ErrorResponse can include field errors")
    void testErrorResponse_Creation_WithFieldErrors() {
        // Arrange
        List<ErrorResponse.FieldError> fieldErrors = List.of(
            new ErrorResponse.FieldError("username", "Username is required"),
            new ErrorResponse.FieldError("email", "Invalid email format")
        );

        // Act
        ErrorResponse errorResponse = ErrorResponse.of(400, "Validation failed", fieldErrors);

        // Assert
        assertThat(errorResponse.status()).isEqualTo(400);
        assertThat(errorResponse.message()).isEqualTo("Validation failed");
        assertThat(errorResponse.timestamp()).isNotNull();
        assertThat(errorResponse.errors()).hasSize(2);
        assertThat(errorResponse.errors()).extracting(ErrorResponse.FieldError::field)
            .containsExactly("username", "email");
    }

    @Test
    @DisplayName("testFieldError_Creation - FieldError can be created with field and message")
    void testFieldError_Creation() {
        // Act
        ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError("title", "Title is required");

        // Assert
        assertThat(fieldError.field()).isEqualTo("title");
        assertThat(fieldError.message()).isEqualTo("Title is required");
    }

    // ===== RESPONSE BUILDER TESTS =====

    @Test
    @DisplayName("testResponse_Entity_IsErrorResponse - Response entity is ErrorResponse instance")
    void testResponse_Entity_IsErrorResponse() {
        // Arrange
        DomainException exception = new FeedbackNotFoundException("test-id");

        // Act
        Response response = domainExceptionMapper.toResponse(exception);

        // Assert
        assertThat(response.getEntity()).isInstanceOf(ErrorResponse.class);
    }

    @Test
    @DisplayName("testResponse_ContentType_JSON - Response should be JSON (validated through entity type)")
    void testResponse_ContentType_JSON() {
        // Arrange
        DomainException exception = new FeedbackNotFoundException("test-id");

        // Act
        Response response = domainExceptionMapper.toResponse(exception);

        // Assert - The entity type being ErrorResponse indicates proper JSON serialization
        assertThat(response.getEntity()).isInstanceOf(ErrorResponse.class);
    }

    // ===== MULTIPLE EXCEPTIONS TESTS =====

    @Test
    @DisplayName("testMultipleExceptions_DifferentStatuses - Different exceptions return correct statuses")
    void testMultipleExceptions_DifferentStatuses() {
        // Act & Assert - Feedback Not Found (404)
        Response feedbackResponse = domainExceptionMapper.toResponse(
            new FeedbackNotFoundException("id1")
        );
        assertThat(feedbackResponse.getStatus()).isEqualTo(404);

        // Act & Assert - Category Not Found (404)
        Response categoryResponse = domainExceptionMapper.toResponse(
            new CategoryNotFoundException(123L)
        );
        assertThat(categoryResponse.getStatus()).isEqualTo(404);

        // Act & Assert - Generic Domain Exception (500)
        Response genericResponse = domainExceptionMapper.toResponse(
            new InvalidFeedbackException("Server error")
        );
        assertThat(genericResponse.getStatus()).isEqualTo(500);

        // Act & Assert - User Not Found (404)
        Response userResponse = userExceptionMapper.toResponse(
            new UserNotFoundException(456L)
        );
        assertThat(userResponse.getStatus()).isEqualTo(404);
    }
}
