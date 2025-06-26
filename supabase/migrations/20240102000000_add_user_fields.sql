-- Add new fields to User table for comprehensive user registration
ALTER TABLE "public"."User" 
ADD COLUMN IF NOT EXISTS "name" character varying,
ADD COLUMN IF NOT EXISTS "phone" character varying,
ADD COLUMN IF NOT EXISTS "height" double precision,
ADD COLUMN IF NOT EXISTS "weight" double precision,
ADD COLUMN IF NOT EXISTS "chronic_diseases" text,
ADD COLUMN IF NOT EXISTS "injury_history" text,
ADD COLUMN IF NOT EXISTS "role" character varying DEFAULT 'MEMBER';

-- Create index for better performance on login queries
CREATE INDEX IF NOT EXISTS idx_user_username ON "public"."User" ("username");
CREATE INDEX IF NOT EXISTS idx_user_email ON "public"."User" ("email");
CREATE INDEX IF NOT EXISTS idx_user_role ON "public"."User" ("role");

-- Update existing users to have default role
UPDATE "public"."User" SET "role" = 'MEMBER' WHERE "role" IS NULL; 