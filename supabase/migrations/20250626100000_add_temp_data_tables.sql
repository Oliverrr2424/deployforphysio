-- Add temporary data tables for user preferences and generated plans

-- Create user_preferences table
CREATE TABLE IF NOT EXISTS "public"."user_preferences" (
    "preference_id" uuid DEFAULT gen_random_uuid() NOT NULL,
    "user_id" uuid NOT NULL,
    "fitness_level" character varying,
    "workout_duration" integer,
    "equipment_access" text,
    "injury_considerations" text,
    "fitness_goals" text,
    "created_at" timestamp without time zone DEFAULT now(),
    "expires_at" timestamp without time zone DEFAULT (now() + interval '7 days'),
    "is_active" boolean DEFAULT true,
    CONSTRAINT "user_preferences_pkey" PRIMARY KEY ("preference_id")
);

-- Create generated_plans table
CREATE TABLE IF NOT EXISTS "public"."generated_plans" (
    "plan_id" uuid DEFAULT gen_random_uuid() NOT NULL,
    "user_id" uuid NOT NULL,
    "plan_data" text,
    "created_at" timestamp without time zone DEFAULT now(),
    "expires_at" timestamp without time zone DEFAULT (now() + interval '1 day'),
    "is_active" boolean DEFAULT true,
    "is_used" boolean DEFAULT false,
    CONSTRAINT "generated_plans_pkey" PRIMARY KEY ("plan_id")
);

-- Add foreign key constraints (optional, but good practice)
ALTER TABLE "public"."user_preferences" 
ADD CONSTRAINT "user_preferences_user_id_fkey" 
FOREIGN KEY ("user_id") REFERENCES "public"."User"("user_id") ON DELETE CASCADE;

ALTER TABLE "public"."generated_plans" 
ADD CONSTRAINT "generated_plans_user_id_fkey" 
FOREIGN KEY ("user_id") REFERENCES "public"."User"("user_id") ON DELETE CASCADE;

-- Add indexes for better performance
CREATE INDEX IF NOT EXISTS "idx_user_preferences_user_id" ON "public"."user_preferences"("user_id");
CREATE INDEX IF NOT EXISTS "idx_user_preferences_active" ON "public"."user_preferences"("is_active", "expires_at");
CREATE INDEX IF NOT EXISTS "idx_generated_plans_user_id" ON "public"."generated_plans"("user_id");
CREATE INDEX IF NOT EXISTS "idx_generated_plans_active" ON "public"."generated_plans"("is_active", "expires_at");

-- Set ownership
ALTER TABLE "public"."user_preferences" OWNER TO "postgres";
ALTER TABLE "public"."generated_plans" OWNER TO "postgres"; 