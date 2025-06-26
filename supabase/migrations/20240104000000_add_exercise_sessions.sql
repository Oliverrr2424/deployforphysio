-- Add exercise sessions table for tracking user workouts
-- This table stores completed exercise sessions with detailed logging

-- Exercise Sessions table for storing completed workout sessions
CREATE TABLE IF NOT EXISTS "public"."exercise_sessions" (
    "session_id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "user_id" "uuid" NOT NULL,
    "exercise_name" character varying(255) NOT NULL,
    "sets" integer NOT NULL DEFAULT 0,
    "reps" integer NOT NULL DEFAULT 0,
    "weight" decimal(5,2) DEFAULT 0,
    "duration_seconds" integer DEFAULT 0,
    "session_date" date NOT NULL DEFAULT CURRENT_DATE,
    "notes" text,
    "completed" boolean DEFAULT false,
    "plan_name" character varying(255),
    "exercise_order" integer DEFAULT 0,
    "created_at" timestamp with time zone DEFAULT NOW(),
    "updated_at" timestamp with time zone DEFAULT NOW(),
    PRIMARY KEY ("session_id")
);

-- Exercise Set Details table for tracking individual sets within exercises
CREATE TABLE IF NOT EXISTS "public"."exercise_set_details" (
    "set_id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "session_id" "uuid" NOT NULL,
    "set_number" integer NOT NULL,
    "reps_completed" integer NOT NULL DEFAULT 0,
    "weight_used" decimal(5,2) DEFAULT 0,
    "duration_seconds" integer DEFAULT 0,
    "completed" boolean DEFAULT false,
    "created_at" timestamp with time zone DEFAULT NOW(),
    PRIMARY KEY ("set_id")
);

-- Workout Sessions table for tracking overall workout sessions
CREATE TABLE IF NOT EXISTS "public"."workout_sessions" (
    "workout_id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "user_id" "uuid" NOT NULL,
    "session_name" character varying(255),
    "session_date" date NOT NULL DEFAULT CURRENT_DATE,
    "start_time" timestamp with time zone DEFAULT NOW(),
    "end_time" timestamp with time zone,
    "total_duration_seconds" integer DEFAULT 0,
    "exercises_completed" integer DEFAULT 0,
    "exercises_planned" integer DEFAULT 0,
    "notes" text,
    "completed" boolean DEFAULT false,
    "created_at" timestamp with time zone DEFAULT NOW(),
    "updated_at" timestamp with time zone DEFAULT NOW(),
    PRIMARY KEY ("workout_id")
);

-- Add foreign key constraints
ALTER TABLE "public"."exercise_sessions" 
    ADD CONSTRAINT "exercise_sessions_user_id_fkey" 
    FOREIGN KEY ("user_id") REFERENCES "public"."User"("user_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "public"."exercise_set_details" 
    ADD CONSTRAINT "exercise_set_details_session_id_fkey" 
    FOREIGN KEY ("session_id") REFERENCES "public"."exercise_sessions"("session_id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "public"."workout_sessions" 
    ADD CONSTRAINT "workout_sessions_user_id_fkey" 
    FOREIGN KEY ("user_id") REFERENCES "public"."User"("user_id") ON UPDATE CASCADE ON DELETE CASCADE;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS "idx_exercise_sessions_user_id" ON "public"."exercise_sessions" ("user_id");
CREATE INDEX IF NOT EXISTS "idx_exercise_sessions_date" ON "public"."exercise_sessions" ("session_date");
CREATE INDEX IF NOT EXISTS "idx_exercise_sessions_created_at" ON "public"."exercise_sessions" ("created_at");
CREATE INDEX IF NOT EXISTS "idx_exercise_sessions_exercise_name" ON "public"."exercise_sessions" ("exercise_name");

CREATE INDEX IF NOT EXISTS "idx_exercise_set_details_session_id" ON "public"."exercise_set_details" ("session_id");
CREATE INDEX IF NOT EXISTS "idx_exercise_set_details_set_number" ON "public"."exercise_set_details" ("set_number");

CREATE INDEX IF NOT EXISTS "idx_workout_sessions_user_id" ON "public"."workout_sessions" ("user_id");
CREATE INDEX IF NOT EXISTS "idx_workout_sessions_date" ON "public"."workout_sessions" ("session_date");
CREATE INDEX IF NOT EXISTS "idx_workout_sessions_created_at" ON "public"."workout_sessions" ("created_at");

-- Enable Row Level Security
ALTER TABLE "public"."exercise_sessions" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "public"."exercise_set_details" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "public"."workout_sessions" ENABLE ROW LEVEL SECURITY;

-- Grant permissions
GRANT ALL ON TABLE "public"."exercise_sessions" TO "anon";
GRANT ALL ON TABLE "public"."exercise_sessions" TO "authenticated";
GRANT ALL ON TABLE "public"."exercise_sessions" TO "service_role";

GRANT ALL ON TABLE "public"."exercise_set_details" TO "anon";
GRANT ALL ON TABLE "public"."exercise_set_details" TO "authenticated";
GRANT ALL ON TABLE "public"."exercise_set_details" TO "service_role";

GRANT ALL ON TABLE "public"."workout_sessions" TO "anon";
GRANT ALL ON TABLE "public"."workout_sessions" TO "authenticated";
GRANT ALL ON TABLE "public"."workout_sessions" TO "service_role";

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for updating timestamps
CREATE TRIGGER update_exercise_sessions_updated_at 
    BEFORE UPDATE ON "public"."exercise_sessions"
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_workout_sessions_updated_at 
    BEFORE UPDATE ON "public"."workout_sessions"
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Create function to get user exercise statistics
CREATE OR REPLACE FUNCTION get_user_exercise_stats(user_uuid uuid, start_date date DEFAULT NULL, end_date date DEFAULT NULL)
RETURNS TABLE (
    total_sessions bigint,
    total_exercises bigint,
    total_sets bigint,
    avg_sets_per_exercise numeric,
    most_frequent_exercise text,
    workout_streak integer
) AS $$
BEGIN
    RETURN QUERY
    WITH session_stats AS (
        SELECT 
            COUNT(DISTINCT es.session_id) as sessions,
            COUNT(*) as exercises,
            SUM(es.sets) as total_sets,
            AVG(es.sets::numeric) as avg_sets
        FROM exercise_sessions es
        WHERE es.user_id = user_uuid
        AND (start_date IS NULL OR es.session_date >= start_date)
        AND (end_date IS NULL OR es.session_date <= end_date)
    ),
    exercise_frequency AS (
        SELECT 
            es.exercise_name,
            COUNT(*) as frequency
        FROM exercise_sessions es
        WHERE es.user_id = user_uuid
        AND (start_date IS NULL OR es.session_date >= start_date)
        AND (end_date IS NULL OR es.session_date <= end_date)
        GROUP BY es.exercise_name
        ORDER BY frequency DESC
        LIMIT 1
    ),
    streak_calc AS (
        SELECT COUNT(*) as streak
        FROM (
            SELECT session_date,
                   ROW_NUMBER() OVER (ORDER BY session_date DESC) as rn,
                   session_date - INTERVAL '1 day' * ROW_NUMBER() OVER (ORDER BY session_date DESC) as streak_group
            FROM (
                SELECT DISTINCT session_date
                FROM exercise_sessions
                WHERE user_id = user_uuid
                AND session_date <= CURRENT_DATE
                ORDER BY session_date DESC
            ) daily_sessions
        ) grouped
        WHERE streak_group = (
            SELECT session_date - INTERVAL '1 day' * ROW_NUMBER() OVER (ORDER BY session_date DESC)
            FROM (
                SELECT DISTINCT session_date
                FROM exercise_sessions
                WHERE user_id = user_uuid
                AND session_date <= CURRENT_DATE
                ORDER BY session_date DESC
                LIMIT 1
            ) latest
        )
    )
    SELECT 
        ss.sessions,
        ss.exercises,
        ss.total_sets,
        ss.avg_sets,
        COALESCE(ef.exercise_name, 'None'::text),
        COALESCE(sc.streak, 0)
    FROM session_stats ss
    CROSS JOIN exercise_frequency ef
    CROSS JOIN streak_calc sc;
END;
$$ LANGUAGE plpgsql; 