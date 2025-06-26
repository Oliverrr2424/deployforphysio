-- Update foreign key references to use the lowercase "user" table
-- This matches what Hibernate creates at runtime

-- Drop existing foreign key constraints that reference "User" table
ALTER TABLE "public"."exercise_sessions" DROP CONSTRAINT IF EXISTS "exercise_sessions_user_id_fkey";
ALTER TABLE "public"."workout_sessions" DROP CONSTRAINT IF EXISTS "workout_sessions_user_id_fkey";

-- We'll let Hibernate handle creating the user table and foreign keys at runtime
-- This migration just removes the conflicting constraints 