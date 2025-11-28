# Development Notes - Feedback Feature

## Current Status (2025-11-20)

### Recently Completed
- ✅ Analyzed OpenAPI spec compliance for feedback endpoints
- ✅ Added SmallRye OpenAPI annotations to `FeedbackResource`
- ✅ Updated `CLAUDE.md` with improved architecture documentation

### Key Architectural Decisions

#### Vote Count Storage Strategy
**Decision**: Use **denormalized counts** (store in feedback table) with a separate votes tracking table

**Rationale**:
- Read-to-write ratio is very high (many views, fewer votes)
- List views need vote counts for many items simultaneously
- Required for sorting by popularity
- 8 bytes per row overhead is negligible vs query performance gains
- OpenAPI spec requires `upvotes` and `downvotes` in response

**Implementation Plan**:
```sql
-- Main table with cached counts
ALTER TABLE feedback
ADD COLUMN upvotes INT DEFAULT 0,
ADD COLUMN downvotes INT DEFAULT 0;

-- Detailed votes table for integrity
CREATE TABLE feedback_vote (
    id BIGSERIAL PRIMARY KEY,
    feedback_id BIGINT REFERENCES feedback(id),
    user_id BIGINT REFERENCES user(id),
    vote_type VARCHAR(10), -- 'UP' or 'DOWN'
    created_at TIMESTAMP,
    UNIQUE(feedback_id, user_id)
);

-- Use PostgreSQL triggers to maintain count consistency
```

## OpenAPI Compliance Issues

### Critical (Must Fix)

#### 1. Status Enum Mismatch 🔴
- **Location**: `src/main/java/com/agora/domain/feedback/model/entity/FeedbackStatus.java`
- **Current**: `OPENED`, `CLOSED`
- **Required**: `PENDING`, `ACKNOWLEDGED`, `IN_PROGRESS`, `COMPLETED`
- **Impact**: Complete API contract incompatibility

#### 2. Path Mismatch 🔴
- **Current**: `/api/feedbacks`
- **Spec**: `/feedback`
- **Decision Needed**: Change path or update spec?

#### 3. Missing Response Fields 🔴
**Location**: `src/main/java/com/agora/domain/feedback/model/dto/FeedbackResponse.java`

Missing required fields:
- `upvotes` (Integer) - Required
- `downvotes` (Integer)
- `commentCount` (Integer) - Required
- `xpEarned` (Integer)
- `updatedAt` (OffsetDateTime)
- `developmentResponse` (String)
- `attachments` (List<Attachment>)
- `author` should be full User object, not just ID and name

#### 4. Missing List Endpoint Features 🔴
**Location**: `src/main/java/com/agora/domain/feedback/resource/FeedbackResource.java:48`

Current `GET /api/feedbacks` lacks:
- Pagination (page, pageSize query parameters)
- Filtering (status, category, sentiment)
- Search functionality (search in title/description)
- Sorting (sortBy: newest, trending, mostCommented)
- Should return `FeedbackListResponse` with pagination metadata

#### 5. Missing Vote Endpoint 🔴
**Required**: `POST /feedback/{id}/upvote`
- Request body: `{ "direction": "up" | "down" | "none" }`
- Response: `{ "upvotes": int, "downvotes": int, "userVote": string }`
- Needs `VoteRequest` DTO and vote tracking table

### High Priority

#### 6. HTTP Method Mismatch 🟡
- **Location**: `FeedbackResource.java:110`
- **Current**: `PUT` for updates
- **Required**: `PATCH`

#### 7. Validation Mismatches 🟡
**Title** (`CreateFeedbackCommand.java:10`):
- Current: 3-255 characters
- Required: 5-200 characters

**Category** (`CreateFeedbackCommand.java:20`):
- Current: Optional
- Required: Required field

**Tags** (`CreateFeedbackCommand.java:26`):
- Current: Single String
- Required: Array of strings, max 5 items

#### 8. Status Auto-Assignment 🟡
**Location**: `CreateFeedbackCommand.java:17-18`
- Current: Status is required in create request
- Required: Status should be auto-assigned (default to "pending")

### Medium Priority

#### 9. Extra Endpoints (Not in Spec) 🟠
- `POST /api/feedbacks/{id}/archive`
- `POST /api/feedbacks/{id}/reopen`
- **Decision Needed**: Keep or remove? Update spec?

## Next Steps

### Phase 1: Data Model Updates
1. [ ] Create Flyway migration to add vote count columns to feedback table
2. [ ] Create `feedback_vote` table with user_id, feedback_id, vote_type
3. [ ] Add database triggers for automatic count updates
4. [ ] Update `FeedbackStatus` enum values
5. [ ] Add missing fields to `FeedbackResponse`
6. [ ] Update `Feedback` entity with new fields

### Phase 2: Vote Feature Implementation
1. [ ] Create `Vote` entity
2. [ ] Create `VoteRepository`
3. [ ] Create `VoteRequest` DTO
4. [ ] Create `VoteResponse` DTO
5. [ ] Implement vote logic in application service
6. [ ] Add `POST /{id}/upvote` endpoint to `FeedbackResource`
7. [ ] Add tests for voting functionality

### Phase 3: List Endpoint Enhancement
1. [ ] Create pagination DTOs (PageRequest, PageResponse)
2. [ ] Add query parameters to listAll endpoint (status, category, sentiment, search, sortBy, page, pageSize)
3. [ ] Implement filtering logic in repository
4. [ ] Implement search functionality (title/description)
5. [ ] Implement sorting options (newest, trending, mostCommented)
6. [ ] Update response to use `FeedbackListResponse` with pagination metadata

### Phase 4: Validation & Request Updates
1. [ ] Update title validation (5-200 chars)
2. [ ] Make category required in `CreateFeedbackCommand`
3. [ ] Change tags from String to List<String> with max 5 items
4. [ ] Remove status from `CreateFeedbackCommand` (auto-assign "pending")
5. [ ] Update tests for new validations

### Phase 5: Path & Method Updates
1. [ ] Decide on path: change to `/feedback` or keep `/api/feedbacks`?
2. [ ] Change PUT to PATCH for update endpoint
3. [ ] Decide on archive/reopen endpoints: keep or remove?

## Files Modified So Far
- `CLAUDE.md` - Enhanced architecture documentation
- `FeedbackResource.java` - Added OpenAPI annotations

## Files That Need Changes
- `FeedbackStatus.java` - Update enum values
- `FeedbackResponse.java` - Add missing fields
- `CreateFeedbackCommand.java` - Fix validations, remove status, change tags type
- `UpdateFeedbackCommand.java` - Review and align with spec
- `Feedback.java` - Add new fields (upvotes, downvotes, etc.)
- `FeedbackRepository.java` - Add filtering/search/pagination methods
- `FeedbackApplicationService.java` - Implement new business logic
- Database migrations - New vote table, add columns to feedback

## Testing Strategy
- Update existing tests in `FeedbackResourceTest.java`
- Add new tests for:
  - Voting functionality
  - Pagination and filtering
  - New validation rules
  - Status enum values
  - Edge cases (concurrent votes, vote changes)

## Notes
- Using PostgreSQL 16.2 with Flyway migrations
- Database has `clean-at-start: true` (dev only - drop all objects on startup)
- Using Quarkus Panache for ORM
- Application services handle transactions, not resources
- Consider adding a scheduled job for vote count reconciliation
- Need to decide if voting requires authentication

## OpenAPI Spec Location
- Primary: `/api-spec/openapi.yaml`
- Copy: `/openapi.yaml`
- Both files are identical

## Questions to Address
1. Should we keep the `/archive` and `/reopen` endpoints?
2. Should path be `/feedback` or `/api/feedbacks`?
3. Does voting require authentication?
4. Should we implement XP earning on feedback submission?
5. Do we need the comment count feature now or later?
6. What about attachments support?
