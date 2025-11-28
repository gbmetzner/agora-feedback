# Test Improvement Plan



**Generated:** 2025-11-28

**Codebase:** agora-feedback

**Current Test Coverage:** 1 test file, 8 tests covering 37 Java source files



## Executive Summary



The test suite has significant gaps across all layers (resource, service, repository, entity). This plan provides a prioritized roadmap to improve test coverage and quality.



---



## Phase 1: Critical Foundation (High Priority)



### Task 1.1: Create FeedbackApplicationService Unit Tests



**File to create:** `src/test/java/com/agora/domain/feedback/application/FeedbackApplicationServiceTest.java`



**Requirements:**

- Use `@QuarkusTest` annotation

- Mock repositories with `@InjectMock`

- Test all public methods in `FeedbackApplicationService`

- Cover happy paths and error cases



**Test cases to implement:**

1. `testCreateFeedback_Success()` - Valid feedback creation

2. `testCreateFeedback_WithCategory()` - Feedback with valid category

3. `testCreateFeedback_InvalidCategory()` - Throws `CategoryNotFoundException`

4. `testCreateFeedback_InvalidAuthor()` - Throws `UserNotFoundException`

5. `testUpdateFeedback_Success()` - Update existing feedback

6. `testUpdateFeedback_NotFound()` - Throws `FeedbackNotFoundException`

7. `testUpdateFeedback_ChangeCategory()` - Category update

8. `testUpdateFeedback_RemoveCategory()` - Set category to null

9. `testGetFeedback_Success()` - Retrieve existing feedback

10. `testGetFeedback_NotFound()` - Throws `FeedbackNotFoundException`

11. `testGetAllFeedbacksPaginated_DefaultParams()` - Default pagination

12. `testGetAllFeedbacksPaginated_CustomParams()` - Custom page size and number

13. `testGetAllFeedbacksPaginated_SortOldest()` - Sort by oldest first

14. `testGetAllFeedbacksPaginated_MaxPageSize()` - Enforce max 100 items

15. `testDeleteFeedback_Success()` - Delete existing feedback

16. `testDeleteFeedback_NotFound()` - Throws `FeedbackNotFoundException`

17. `testArchiveFeedback_Success()` - Archive feedback

18. `testArchiveFeedback_NotFound()` - Throws `FeedbackNotFoundException`

19. `testReopenFeedback_Success()` - Reopen closed feedback

20. `testReopenFeedback_NotFound()` - Throws `FeedbackNotFoundException`

21. `testAddComment_Success()` - Add comment to feedback

22. `testAddComment_FeedbackNotFound()` - Throws `FeedbackNotFoundException`

23. `testAddComment_UserNotFound()` - Throws `UserNotFoundException`

24. `testGetCommentsByFeedbackId_Success()` - Retrieve comments

25. `testGetCommentsByFeedbackId_NotFound()` - Throws `FeedbackNotFoundException`



**Reference:** `src/main/java/com/agora/domain/feedback/application/FeedbackApplicationService.java`



---



### Task 1.2: Complete FeedbackResource Test Coverage



**File to update:** `src/test/java/com/agora/domain/feedback/resource/FeedbackResourceTest.java`



**Add missing endpoint tests:**



1. **Archive endpoint tests:**

    - `testArchiveFeedback_Success()` - POST `/v1/api/feedback/{id}/archive` returns 200

    - `testArchiveFeedback_NotFound()` - Returns 404 for invalid ID

    - `testArchiveFeedback_VerifyArchivedFlag()` - Verify archived=true in response



2. **Reopen endpoint tests:**

    - `testReopenFeedback_Success()` - POST `/v1/api/feedback/{id}/reopen` returns 200

    - `testReopenFeedback_NotFound()` - Returns 404 for invalid ID

    - `testReopenFeedback_VerifyStatus()` - Verify status changed to PENDING



3. **Get comments endpoint tests:**

    - `testGetComments_Success()` - GET `/v1/api/feedback/{id}/comments` returns 200

    - `testGetComments_EmptyList()` - Returns empty array for feedback with no comments

    - `testGetComments_FeedbackNotFound()` - Returns 404 for invalid feedback ID



4. **Add comment endpoint tests:**

    - `testAddComment_Success()` - PUT `/v1/api/feedback/{id}/comments` returns 201

    - `testAddComment_VerifyCommentCount()` - Verify feedback.comments incremented

    - `testAddComment_FeedbackNotFound()` - Returns 404 for invalid feedback ID

    - `testAddComment_BlankText()` - Returns 400 for validation errors



5. **Pagination tests:**

    - `testListAll_WithPagination()` - Test page and pageSize parameters

    - `testListAll_SortByOldest()` - Test sortBy=oldest

    - `testListAll_SortByNewest()` - Test sortBy=newest (default)

    - `testListAll_InvalidPageNumber()` - Verify defaults to page 1

    - `testListAll_ExcessivePageSize()` - Verify capped at 100



6. **Improve existing tests:**

    - Remove hardcoded IDs (`117457749108987393L`) - use test fixtures

    - Standardize request format (prefer command objects over JSON strings)

    - Add test cleanup to prevent data pollution



**Reference:** `src/main/java/com/agora/domain/feedback/resource/FeedbackResource.java`



---



### Task 1.3: Create AuthResource Tests



**File to create:** `src/test/java/com/agora/domain/user/resource/AuthResourceTest.java`



**Requirements:**

- Test Discord OAuth callback endpoint

- Mock `DiscordAuthService`

- Verify redirect behavior



**Test cases to implement:**

1. `testDiscordCallback_Success()` - Valid code returns 307 redirect

2. `testDiscordCallback_VerifyRedirectUrl()` - Check frontend URL with token

3. `testDiscordCallback_UserDenied()` - Error parameter returns 401

4. `testDiscordCallback_MissingCode()` - No code parameter returns 400

5. `testDiscordCallback_NullCode()` - Null code returns 400



**Reference:** `src/main/java/com/agora/domain/user/resource/AuthResource.java:1-47`



---



## Phase 2: Domain Layer Testing (High Priority)



### Task 2.1: Create Feedback Entity Tests



**File to create:** `src/test/java/com/agora/domain/feedback/model/entity/FeedbackTest.java`



**Requirements:**

- Test domain methods

- Verify lifecycle callbacks

- Test validation constraints



**Test cases to implement:**

1. `testArchive_SetsArchivedFlag()` - Verify `archive()` sets archived=true

2. `testReopen_ChangesStatusToPending()` - Verify `reopen()` changes COMPLETED to PENDING

3. `testReopen_OnlyAffectsCompleted()` - Verify other statuses unchanged

4. `testChangeSentiment_UpdatesValue()` - Verify `changeSentiment()` works

5. `testPrePersist_GeneratesId()` - Verify ID generation

6. `testPrePersist_SetsTimestamps()` - Verify createdAt and updatedAt set

7. `testPrePersist_DefaultValues()` - Verify archived=false, status=PENDING

8. `testPreUpdate_UpdatesTimestamp()` - Verify updatedAt changes

9. `testValidation_BlankTitle()` - Verify constraint violation

10. `testValidation_ShortTitle()` - Verify min length 3

11. `testValidation_LongTitle()` - Verify max length 255

12. `testValidation_BlankDescription()` - Verify constraint violation

13. `testValidation_ShortDescription()` - Verify min length 10

14. `testValidation_LongDescription()` - Verify max length 5000

15. `testValidation_NullStatus()` - Verify constraint violation



**Reference:** `src/main/java/com/agora/domain/feedback/model/entity/Feedback.java`



---



### Task 2.2: Create Repository Tests



**Files to create:**

- `src/test/java/com/agora/domain/feedback/model/repository/FeedbackRepositoryTest.java`

- `src/test/java/com/agora/domain/feedback/model/repository/CommentRepositoryTest.java`

- `src/test/java/com/agora/domain/feedback/model/repository/CategoryRepositoryTest.java`



**FeedbackRepositoryTest requirements:**

1. `testFindAll_OrderByCreatedAtDesc()` - Verify default sorting

2. `testFindAll_OrderByCreatedAtAsc()` - Verify ascending sort

3. `testFindAll_WithPagination()` - Verify page and limit work

4. `testCount_ReturnsCorrectTotal()` - Verify count method

5. `testPersist_SavesFeedback()` - Verify entity persisted

6. `testDeleteById_RemovesFeedback()` - Verify deletion



**CommentRepositoryTest requirements:**

1. `testFindByFeedbackId_ReturnsComments()` - Verify custom query method

2. `testFindByFeedbackId_EmptyList()` - Returns empty for no comments

3. `testFindByFeedbackId_OrderByCreatedAt()` - Verify sorting



**CategoryRepositoryTest requirements:**

1. `testFindById_ReturnsCategory()` - Verify lookup works

2. `testFindAll_ReturnsAllCategories()` - Verify list all



---



## Phase 3: Exception Handling (Medium Priority)



### Task 3.1: Create Exception Mapper Tests



**Files to create:**

- `src/test/java/com/agora/domain/feedback/api/exception/DomainExceptionMapperTest.java`

- `src/test/java/com/agora/domain/feedback/api/exception/ValidationExceptionMapperTest.java`

- `src/test/java/com/agora/domain/feedback/api/exception/UserExceptionMapperTest.java`



**DomainExceptionMapperTest requirements:**

1. `testFeedbackNotFoundException_Returns404()` - Verify status code

2. `testFeedbackNotFoundException_ErrorMessage()` - Verify message format

3. `testCategoryNotFoundException_Returns404()` - Verify status code

4. `testCategoryNotFoundException_ErrorMessage()` - Verify message format



**ValidationExceptionMapperTest requirements:**

1. `testConstraintViolation_Returns400()` - Verify status code

2. `testConstraintViolation_ErrorResponse()` - Verify ErrorResponse structure

3. `testConstraintViolation_MultipleErrors()` - Verify all errors included



**UserExceptionMapperTest requirements:**

1. `testUserNotFoundException_Returns404()` - Verify status code

2. `testUserNotFoundException_ErrorMessage()` - Verify message format



---



## Phase 4: Test Infrastructure (Medium Priority)



### Task 4.1: Create Test Data Builders



**File to create:** `src/test/java/com/agora/domain/feedback/testutil/TestDataBuilder.java`



**Requirements:**

- Builder pattern for test entities

- Reasonable defaults

- Fluent API



**Classes to create:**

```java

public class FeedbackTestBuilder {

    public static FeedbackTestBuilder aFeedback()

    public FeedbackTestBuilder withTitle(String title)

    public FeedbackTestBuilder withDescription(String description)

    public FeedbackTestBuilder withStatus(FeedbackStatus status)

    public FeedbackTestBuilder withCategory(FeedbackCategory category)

    public FeedbackTestBuilder withAuthor(User author)

    public Feedback build()

}

 

public class CreateFeedbackCommandBuilder {

    public static CreateFeedbackCommandBuilder aCommand()

    public CreateFeedbackCommandBuilder withTitle(String title)

    public CreateFeedbackCommandBuilder withDescription(String description)

    public CreateFeedbackCommandBuilder withCategoryId(Long categoryId)

    public CreateFeedbackCommandBuilder withAuthorId(Long authorId)

    public CreateFeedbackCommand build()

}

```



**Usage example:**

```java

var feedback = FeedbackTestBuilder.aFeedback()

    .withTitle("Test Title")

    .withStatus(FeedbackStatus.PENDING)

    .build();

```



---



### Task 4.2: Create Test Fixtures



**File to create:** `src/test/resources/test-data.sql`



**Requirements:**

- Sample categories

- Sample users

- Sample feedback items

- Sample comments

- Use consistent, non-hardcoded IDs



**Content:**

```sql

-- Categories

INSERT INTO feedback_category (id, name, description) VALUES

(1, 'Bug Report', 'Report bugs and issues'),

(2, 'Feature Request', 'Request new features'),

(3, 'Improvement', 'Suggest improvements');

 

-- Users (for testing)

INSERT INTO users (id, name, email, discord_id) VALUES

(100, 'Test User', 'test@example.com', 'discord123'),

(101, 'Admin User', 'admin@example.com', 'discord456');

 

-- Sample feedback

INSERT INTO feedback (id, title, description, status, category_id, author_id, created_at, updated_at, archived) VALUES

(1000, 'Sample Feedback 1', 'This is a test feedback item', 'PENDING', 1, 100, NOW(), NOW(), false),

(1001, 'Sample Feedback 2', 'Another test feedback', 'COMPLETED', 2, 101, NOW(), NOW(), false);

```



---



### Task 4.3: Add Test Configuration Profile



**File to create:** `src/test/resources/application-test.properties`



**Requirements:**

- Separate test database configuration

- Disable flyway clean-at-start for tests

- Configure test-specific settings



**Content:**

```properties

# Test database configuration

quarkus.datasource.db-kind=postgresql

quarkus.datasource.devservices.enabled=true

quarkus.datasource.devservices.reuse=true

 

# Flyway - don't clean in tests

quarkus.flyway.clean-at-start=false

quarkus.flyway.migrate-at-start=true

 

# Hibernate - validate only in tests

quarkus.hibernate-orm.database.generation=none

quarkus.hibernate-orm.log.sql=false

 

# Test-specific settings

quarkus.test.continuous-testing=disabled

```



---



## Phase 5: Integration Testing (Lower Priority)



### Task 5.1: Create End-to-End Integration Tests



**File to create:** `src/test/java/com/agora/domain/feedback/integration/FeedbackWorkflowTest.java`



**Requirements:**

- Test complete user workflows

- Use real database (DevServices)

- Verify transaction boundaries

- No mocking



**Test scenarios:**

1. `testCompleteFeedbackLifecycle()` - Create → Update → Archive → Delete

2. `testFeedbackWithComments()` - Create feedback → Add comments → Retrieve

3. `testPaginationWorkflow()` - Create multiple items → Paginate → Verify ordering

4. `testCategoryAssociation()` - Create with category → Update category → Remove category



---



### Task 5.2: Create Database Migration Tests



**File to create:** `src/test/java/com/agora/infrastructure/FlywayMigrationTest.java`



**Requirements:**

- Verify migrations run successfully

- Test migration idempotency

- Verify schema matches entity definitions



**Test cases:**

1. `testMigrationsRunSuccessfully()` - All migrations execute

2. `testMigrationsAreIdempotent()` - Can run twice safely

3. `testSchemaMatchesEntities()` - No schema drift



---



## Phase 6: Code Quality & Coverage (Lower Priority)



### Task 6.1: Add JaCoCo Code Coverage



**File to update:** `build.gradle`



**Add JaCoCo plugin:**

```gradle

plugins {

    id 'jacoco'

}

 

jacoco {

    toolVersion = "0.8.11"

}

 

jacocoTestReport {

    reports {

        xml.required = true

        html.required = true

    }

}

 

test {

    finalizedBy jacocoTestReport

}

```



**Set minimum coverage thresholds:**

```gradle

jacocoTestCoverageVerification {

    violationRules {

        rule {

            limit {

                minimum = 0.80 // 80% coverage

            }

        }

    }

}

 

check.dependsOn jacocoTestCoverageVerification

```



---



### Task 6.2: Standardize Test Dependencies



**File to update:** `build.gradle`



**Add consistent test libraries:**

```gradle

testImplementation 'io.quarkus:quarkus-junit5'

testImplementation 'io.rest-assured:rest-assured'

testImplementation 'org.assertj:assertj-core:3.25.1'

testImplementation 'org.mockito:mockito-core:5.10.0'

testImplementation 'org.mockito:mockito-junit-jupiter:5.10.0'

```



**Remove inconsistent usage:**

- Standardize on AssertJ for assertions (remove Hamcrest usage)

- Use consistent mocking approach



---



## Implementation Order



Execute tasks in this order for maximum impact:



1. **Week 1:** Tasks 1.1, 1.2 (Service + Resource tests)

2. **Week 2:** Tasks 1.3, 2.1 (Auth + Entity tests)

3. **Week 3:** Tasks 2.2, 3.1 (Repository + Exception tests)

4. **Week 4:** Tasks 4.1, 4.2, 4.3 (Test infrastructure)

5. **Week 5:** Tasks 5.1, 5.2 (Integration tests)

6. **Week 6:** Tasks 6.1, 6.2 (Coverage + Quality)



---



## Success Metrics



After completing this plan, you should achieve:



- **Line Coverage:** 80%+ (currently ~5%)

- **Test Count:** 100+ tests (currently 8)

- **Test Files:** 15+ files (currently 1)

- **Untested Components:** 0 (currently 36/37 files)

- **Build Confidence:** High (currently low)

- **Regression Detection:** Strong (currently weak)



---



## Next Steps



To begin implementation, execute tasks in the order specified above. Each task is self-contained and can be completed independently. Start with Phase 1 for immediate impact on code confidence.



For Claude Code to assist with implementation, reference this plan and specify which task to start with (e.g., "Implement Task 1.1: Create FeedbackApplicationService Unit Tests").