-- Fix email duplicate issue by converting empty emails to NULL
-- and ensuring unique constraint allows multiple NULLs

-- First, update any existing empty string emails to NULL
UPDATE "public"."User" 
SET "email" = NULL 
WHERE "email" = '' OR trim("email") = '';

-- Drop the existing unique constraint
ALTER TABLE "public"."User" 
DROP CONSTRAINT IF EXISTS "User_email_key";

-- Recreate the unique constraint (this will allow multiple NULLs by default in PostgreSQL)
ALTER TABLE "public"."User" 
ADD CONSTRAINT "User_email_key" UNIQUE ("email");

-- Add a comment to clarify the behavior
COMMENT ON CONSTRAINT "User_email_key" ON "public"."User" IS 'Unique constraint on email - allows multiple NULL values for users without email addresses'; 