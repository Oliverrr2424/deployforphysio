-- Add temporary data tables for user preferences and generated plans
-- These tables store temporary data with automatic expiration

-- User Preferences table for storing temporary user workout preferences
CREATE TABLE IF NOT EXISTS "public"."user_preferences" (
    "preference_id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "user_id" "uuid" NOT NULL,
    "fitness_level" character varying,
    "workout_duration" integer,
    "equipment_access" "text",
    "injury_considerations" "text",
    "fitness_goals" "text",
    "created_at" timestamp with time zone DEFAULT NOW(),
    "expires_at" timestamp with time zone NOT NULL,
    "is_active" boolean DEFAULT true,
    PRIMARY KEY ("preference_id")
);

-- Generated Plans table for storing temporary AI-generated workout plans
CREATE TABLE IF NOT EXISTS "public"."generated_plans" (
    "plan_id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "user_id" "uuid" NOT NULL,
    "plan_data" "text" NOT NULL,
    "created_at" timestamp with time zone DEFAULT NOW(),
    "expires_at" timestamp with time zone NOT NULL,
    "is_active" boolean DEFAULT true,
    "is_used" boolean DEFAULT false,
    PRIMARY KEY ("plan_id")
);

-- Add foreign key constraints
ALTER TABLE "public"."user_preferences" 
    ADD CONSTRAINT "user_preferences_user_id_fkey" 
    FOREIGN KEY ("user_id") REFERENCES "public"."User"("user_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "public"."generated_plans" 
    ADD CONSTRAINT "generated_plans_user_id_fkey" 
    FOREIGN KEY ("user_id") REFERENCES "public"."User"("user_id") ON UPDATE CASCADE ON DELETE CASCADE;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS "idx_user_preferences_user_id" ON "public"."user_preferences" ("user_id");
CREATE INDEX IF NOT EXISTS "idx_user_preferences_expires_at" ON "public"."user_preferences" ("expires_at");
CREATE INDEX IF NOT EXISTS "idx_user_preferences_active" ON "public"."user_preferences" ("is_active");

CREATE INDEX IF NOT EXISTS "idx_generated_plans_user_id" ON "public"."generated_plans" ("user_id");
CREATE INDEX IF NOT EXISTS "idx_generated_plans_expires_at" ON "public"."generated_plans" ("expires_at");
CREATE INDEX IF NOT EXISTS "idx_generated_plans_active" ON "public"."generated_plans" ("is_active");
CREATE INDEX IF NOT EXISTS "idx_generated_plans_created_at" ON "public"."generated_plans" ("created_at");

-- Enable Row Level Security
ALTER TABLE "public"."user_preferences" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "public"."generated_plans" ENABLE ROW LEVEL SECURITY;

-- Grant permissions
GRANT ALL ON TABLE "public"."user_preferences" TO "anon";
GRANT ALL ON TABLE "public"."user_preferences" TO "authenticated";
GRANT ALL ON TABLE "public"."user_preferences" TO "service_role";

GRANT ALL ON TABLE "public"."generated_plans" TO "anon";
GRANT ALL ON TABLE "public"."generated_plans" TO "authenticated";
GRANT ALL ON TABLE "public"."generated_plans" TO "service_role";

-- Create a function to automatically clean up expired data
CREATE OR REPLACE FUNCTION cleanup_expired_temp_data()
RETURNS void AS $$
BEGIN
    -- Deactivate expired user preferences
    UPDATE "public"."user_preferences" 
    SET "is_active" = false 
    WHERE "expires_at" <= NOW() AND "is_active" = true;
    
    -- Deactivate expired generated plans
    UPDATE "public"."generated_plans" 
    SET "is_active" = false 
    WHERE "expires_at" <= NOW() AND "is_active" = true;
    
    -- Delete very old data (older than 30 days)
    DELETE FROM "public"."user_preferences" 
    WHERE "expires_at" <= NOW() - INTERVAL '30 days';
    
    DELETE FROM "public"."generated_plans" 
    WHERE "expires_at" <= NOW() - INTERVAL '30 days';
END;
$$ LANGUAGE plpgsql;

-- Create a scheduled job to run cleanup every hour (if using pg_cron extension)
-- Note: This requires the pg_cron extension to be enabled
-- SELECT cron.schedule('cleanup-expired-temp-data', '0 * * * *', 'SELECT cleanup_expired_temp_data();'); 