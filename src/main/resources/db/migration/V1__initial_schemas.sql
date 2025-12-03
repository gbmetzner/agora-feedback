-- Create ENUM type for feedback status (PostgreSQL native type)
CREATE TYPE feedback_status AS ENUM ('PENDING', 'ACKNOWLEDGED', 'IN_PROGRESS', 'COMPLETED');

-- User table (main user entity)
-- Properly quoted because 'user' is a PostgreSQL reserved keyword
CREATE TABLE "user" (
    id BIGINT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    discord_id BIGINT NOT NULL UNIQUE,
    discord_username VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    avatar_url VARCHAR(255),
    reputation_score INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    archived_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT chk_user_email_not_empty CHECK (email <> ''),
    CONSTRAINT chk_user_name_not_empty CHECK (name <> '')
);

-- Indexes on unique/foreign key columns
CREATE INDEX idx_user_email ON "user"(email);
CREATE INDEX idx_user_discord_id ON "user"(discord_id);
CREATE INDEX idx_user_discord_username ON "user"(discord_username);

-- Partial index for active (non-archived) users - more efficient than full index
CREATE INDEX idx_user_active ON "user"(id) WHERE archived_at IS NULL;

-- Indexes for temporal queries (descending for most recent first)
CREATE INDEX idx_user_created_at ON "user"(created_at DESC);
CREATE INDEX idx_user_updated_at ON "user"(updated_at DESC);

-- Category table
CREATE TABLE category (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,

    CONSTRAINT chk_category_name_not_empty CHECK (name <> '')
);

-- Feedback table with audit trail
CREATE TABLE feedback (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status feedback_status NOT NULL DEFAULT 'PENDING',
    author_id BIGINT REFERENCES "user"(id) ON DELETE SET NULL,
    category_id BIGINT REFERENCES category(id) ON DELETE SET NULL,
    sentiment VARCHAR(50),
    upvotes INT NOT NULL DEFAULT 0,
    downvotes INT NOT NULL DEFAULT 0,
    comments INT NOT NULL DEFAULT 0,
    tags VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    archived BOOLEAN NOT NULL DEFAULT FALSE,

    -- Explicit named constraints for better clarity
    CONSTRAINT chk_feedback_title_not_empty CHECK (title <> ''),
    CONSTRAINT chk_feedback_description_not_empty CHECK (description <> ''),
    CONSTRAINT chk_feedback_upvotes_non_negative CHECK (upvotes >= 0),
    CONSTRAINT chk_feedback_downvotes_non_negative CHECK (downvotes >= 0),
    CONSTRAINT chk_feedback_comments_non_negative CHECK (comments >= 0)
);

-- Indexes on foreign keys (required for join performance)
CREATE INDEX idx_feedback_author_id ON feedback(author_id);
CREATE INDEX idx_feedback_category_id ON feedback(category_id);

-- Indexes for common query patterns
CREATE INDEX idx_feedback_status ON feedback(status);
CREATE INDEX idx_feedback_created_at ON feedback(created_at DESC);

-- Partial index for active feedback (most common query pattern)
-- More efficient than full index on archived column
CREATE INDEX idx_feedback_active ON feedback(id) WHERE NOT archived;