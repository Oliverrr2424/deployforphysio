-- Migration to add exercise status tracking and real-time calendar functionality
-- Created: 2025-06-22

-- Add status column to exercise_sessions table
ALTER TABLE exercise_sessions 
ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'completed' CHECK (status IN ('planned', 'in_progress', 'completed', 'skipped'));

-- Add scheduled_date column for future exercises  
ALTER TABLE exercise_sessions
ADD COLUMN IF NOT EXISTS scheduled_date DATE DEFAULT CURRENT_DATE;

-- Create planned_exercises table for future workout planning
CREATE TABLE IF NOT EXISTS planned_exercises (
    plan_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    exercise_name VARCHAR(255) NOT NULL,
    planned_sets INTEGER DEFAULT 0,
    planned_reps VARCHAR(50),
    planned_weight DECIMAL(5,2) DEFAULT 0,
    planned_duration_seconds INTEGER DEFAULT 0,
    scheduled_date DATE NOT NULL,
    scheduled_time TIME,
    status VARCHAR(20) DEFAULT 'planned' CHECK (status IN ('planned', 'in_progress', 'completed', 'skipped', 'cancelled')),
    notes TEXT,
    plan_name VARCHAR(255),
    exercise_order INTEGER DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_planned_exercises_user_date ON planned_exercises(user_id, scheduled_date);
CREATE INDEX IF NOT EXISTS idx_planned_exercises_status ON planned_exercises(status);
CREATE INDEX IF NOT EXISTS idx_exercise_sessions_status ON exercise_sessions(status);
CREATE INDEX IF NOT EXISTS idx_exercise_sessions_scheduled_date ON exercise_sessions(scheduled_date);

-- Create trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_planned_exercises_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_planned_exercises_updated_at
    BEFORE UPDATE ON planned_exercises
    FOR EACH ROW
    EXECUTE FUNCTION update_planned_exercises_updated_at();

-- Function to get calendar data with all exercise types
CREATE OR REPLACE FUNCTION get_user_calendar_data(
    p_user_id UUID,
    p_start_date DATE,
    p_end_date DATE
)
RETURNS TABLE (
    calendar_date DATE,
    exercise_type VARCHAR(20),
    exercise_count INTEGER,
    total_duration INTEGER,
    exercise_names TEXT[],
    status_summary JSONB
) AS $$
BEGIN
    RETURN QUERY
    WITH completed_exercises AS (
        SELECT 
            session_date as calendar_date,
            'completed' as exercise_type,
            COUNT(*) as exercise_count,
            SUM(duration_seconds) as total_duration,
            ARRAY_AGG(exercise_name) as exercise_names,
            jsonb_object_agg(status, COUNT(*)) as status_summary
        FROM exercise_sessions 
        WHERE user_id = p_user_id 
        AND session_date BETWEEN p_start_date AND p_end_date
        AND status IN ('completed', 'skipped')
        GROUP BY session_date
    ),
    planned_exercises_data AS (
        SELECT 
            scheduled_date as calendar_date,
            'planned' as exercise_type,
            COUNT(*) as exercise_count,
            SUM(planned_duration_seconds) as total_duration,
            ARRAY_AGG(exercise_name) as exercise_names,
            jsonb_object_agg(status, COUNT(*)) as status_summary
        FROM planned_exercises 
        WHERE user_id = p_user_id 
        AND scheduled_date BETWEEN p_start_date AND p_end_date
        AND status = 'planned'
        GROUP BY scheduled_date
    )
    SELECT * FROM completed_exercises
    UNION ALL
    SELECT * FROM planned_exercises_data
    ORDER BY calendar_date DESC;
END;
$$ LANGUAGE plpgsql;

-- Function to convert planned exercise to completed when workout starts
CREATE OR REPLACE FUNCTION start_planned_workout(
    p_user_id UUID,
    p_plan_id UUID
)
RETURNS UUID AS $$
DECLARE
    workout_session_id UUID;
BEGIN
    -- Create workout session
    INSERT INTO workout_sessions (
        user_id, session_name, session_date, start_time, 
        total_duration_seconds, exercises_completed, exercises_planned, completed
    )
    VALUES (
        p_user_id,
        'Planned Workout - ' || CURRENT_DATE,
        CURRENT_DATE,
        NOW(),
        0, 0, 
        (SELECT COUNT(*) FROM planned_exercises WHERE plan_id = p_plan_id),
        false
    )
    RETURNING workout_id INTO workout_session_id;
    
    -- Update planned exercises status to in_progress
    UPDATE planned_exercises 
    SET status = 'in_progress', updated_at = NOW()
    WHERE plan_id = p_plan_id AND user_id = p_user_id;
    
    RETURN workout_session_id;
END;
$$ LANGUAGE plpgsql; 