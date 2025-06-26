-- Make email required and enforce proper uniqueness
-- This reverses the previous nullable email approach

-- Drop the existing constraint that allows nulls
ALTER TABLE "public"."User" 
DROP CONSTRAINT IF EXISTS "User_email_key";

-- Update any NULL emails to empty strings (temporary)
UPDATE "public"."User" 
SET "email" = '' 
WHERE "email" IS NULL;

-- Make email NOT NULL
ALTER TABLE "public"."User" 
ALTER COLUMN "email" SET NOT NULL;

-- Add unique constraint back
ALTER TABLE "public"."User" 
ADD CONSTRAINT "User_email_key" UNIQUE ("email");

-- Add a comment explaining the new behavior
COMMENT ON CONSTRAINT "User_email_key" ON "public"."User" IS 'Unique constraint on email - email is required and must be unique'; 