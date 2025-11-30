package com.agora.domain.feedback.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for FeedbackCategory entity.
 * <p>
 * Tests entity constructors and field management without database persistence.
 * </p>
 */
@DisplayName("FeedbackCategory Entity Tests")
class FeedbackCategoryEntityTest {

    // ===== CONSTRUCTOR TESTS =====

    @Test
    @DisplayName("testDefaultConstructor - Creates empty FeedbackCategory")
    void testDefaultConstructor() {
        // Act
        FeedbackCategory category = new FeedbackCategory();

        // Assert
        assertThat(category).isNotNull();
        assertThat(category.getId()).isNull();
        assertThat(category.getName()).isNull();
    }

    @Test
    @DisplayName("testNameConstructor - Creates FeedbackCategory with name only")
    void testNameConstructor() {
        // Act
        FeedbackCategory category = new FeedbackCategory("Bug Report");

        // Assert
        assertThat(category.getId()).isNull();
        assertThat(category.getName()).isEqualTo("Bug Report");
    }

    @Test
    @DisplayName("testFullConstructor - Creates FeedbackCategory with id and name")
    void testFullConstructor() {
        // Act
        FeedbackCategory category = new FeedbackCategory(1L, "Feature Request");

        // Assert
        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Feature Request");
    }

    // ===== FIELD SETTER TESTS =====

    @Test
    @DisplayName("testSetId_UpdatesId - ID field can be updated")
    void testSetId_UpdatesId() {
        // Arrange
        FeedbackCategory category = new FeedbackCategory("Bug Report");

        // Act
        category.setId(100L);

        // Assert
        assertThat(category.getId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("testSetName_UpdatesName - Name field can be updated")
    void testSetName_UpdatesName() {
        // Arrange
        FeedbackCategory category = new FeedbackCategory("Bug Report");

        // Act
        category.setName("Critical Issue");

        // Assert
        assertThat(category.getName()).isEqualTo("Critical Issue");
    }

    // ===== GETTER TESTS =====

    @Test
    @DisplayName("testGetId_ReturnsId - ID getter returns correct value")
    void testGetId_ReturnsId() {
        // Arrange
        FeedbackCategory category = new FeedbackCategory(42L, "Enhancement");

        // Act & Assert
        assertThat(category.getId()).isEqualTo(42L);
    }

    @Test
    @DisplayName("testGetName_ReturnsName - Name getter returns correct value")
    void testGetName_ReturnsName() {
        // Arrange
        FeedbackCategory category = new FeedbackCategory("Documentation");

        // Act & Assert
        assertThat(category.getName()).isEqualTo("Documentation");
    }

    // ===== MULTIPLE INSTANTIATION TESTS =====

    @Test
    @DisplayName("testMultipleInstances_Independent - Multiple categories are independent")
    void testMultipleInstances_Independent() {
        // Arrange
        FeedbackCategory category1 = new FeedbackCategory("Bug Report");
        FeedbackCategory category2 = new FeedbackCategory("Feature Request");
        FeedbackCategory category3 = new FeedbackCategory("Documentation");

        // Act & Assert
        assertThat(category1.getName()).isEqualTo("Bug Report");
        assertThat(category2.getName()).isEqualTo("Feature Request");
        assertThat(category3.getName()).isEqualTo("Documentation");

        // Verify they are different instances
        assertThat(category1).isNotEqualTo(category2);
        assertThat(category2).isNotEqualTo(category3);
    }

    @Test
    @DisplayName("testIdAssignment_Different - Each category can have different ID")
    void testIdAssignment_Different() {
        // Arrange & Act
        FeedbackCategory cat1 = new FeedbackCategory(1L, "Category 1");
        FeedbackCategory cat2 = new FeedbackCategory(2L, "Category 2");
        FeedbackCategory cat3 = new FeedbackCategory(3L, "Category 3");

        // Assert
        assertThat(cat1.getId()).isEqualTo(1L);
        assertThat(cat2.getId()).isEqualTo(2L);
        assertThat(cat3.getId()).isEqualTo(3L);
    }

    // ===== NAME UPDATE TESTS =====

    @Test
    @DisplayName("testNameUpdate_Multiple - Name can be updated multiple times")
    void testNameUpdate_Multiple() {
        // Arrange
        FeedbackCategory category = new FeedbackCategory("Initial Name");

        // Act & Assert
        assertThat(category.getName()).isEqualTo("Initial Name");

        category.setName("Updated Name 1");
        assertThat(category.getName()).isEqualTo("Updated Name 1");

        category.setName("Updated Name 2");
        assertThat(category.getName()).isEqualTo("Updated Name 2");

        category.setName("Final Name");
        assertThat(category.getName()).isEqualTo("Final Name");
    }

    @Test
    @DisplayName("testNameUpdate_Null - Name can be set to null")
    void testNameUpdate_Null() {
        // Arrange
        FeedbackCategory category = new FeedbackCategory("Bug Report");

        // Act
        category.setName(null);

        // Assert
        assertThat(category.getName()).isNull();
    }

    @Test
    @DisplayName("testNameUpdate_Empty - Name can be set to empty string")
    void testNameUpdate_Empty() {
        // Arrange
        FeedbackCategory category = new FeedbackCategory("Bug Report");

        // Act
        category.setName("");

        // Assert
        assertThat(category.getName()).isEmpty();
    }

    // ===== SPECIAL CHARACTER HANDLING TESTS =====

    @Test
    @DisplayName("testNameWithSpecialCharacters - Name can contain special characters")
    void testNameWithSpecialCharacters() {
        // Act
        FeedbackCategory category = new FeedbackCategory("Bug/Issue #1 (Critical)");

        // Assert
        assertThat(category.getName()).isEqualTo("Bug/Issue #1 (Critical)");
    }

    @Test
    @DisplayName("testNameWithSpaces - Name can contain spaces")
    void testNameWithSpaces() {
        // Act
        FeedbackCategory category = new FeedbackCategory("Feature Request");

        // Assert
        assertThat(category.getName()).isEqualTo("Feature Request");
    }

    @Test
    @DisplayName("testNameWithUnicode - Name can contain unicode characters")
    void testNameWithUnicode() {
        // Act
        FeedbackCategory category = new FeedbackCategory("Bug Report - 報告");

        // Assert
        assertThat(category.getName()).isEqualTo("Bug Report - 報告");
    }
}
