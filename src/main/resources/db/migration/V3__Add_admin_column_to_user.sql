-- Add role column to user table for role-based authorization
ALTER TABLE "user" ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'USER';

-- Create index on role for efficient queries
CREATE INDEX idx_user_role ON "user"(role);
