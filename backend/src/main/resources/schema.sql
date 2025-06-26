-- User table schema for PostgreSQL
CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255),
    name VARCHAR(255),
    gender VARCHAR(50),
    age INTEGER,
    phone VARCHAR(20),
    height DOUBLE PRECISION,
    weight DOUBLE PRECISION,
    chronic_diseases TEXT,
    injury_history TEXT,
    fitness_goal TEXT,
    equipment_access TEXT,
    role VARCHAR(50) DEFAULT 'MEMBER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Create Exercise table
CREATE TABLE IF NOT EXISTS exercise (
    exercise_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    target_muscles TEXT,
    secondary_muscles TEXT,
    equipment_required TEXT,
    difficulty_level VARCHAR(50),
    image_url TEXT,
    video_url TEXT
);

-- Create Injury table
CREATE TABLE IF NOT EXISTS injury (
    injury_id SERIAL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    injury_type VARCHAR(255),
    injury_area VARCHAR(255),
    severity VARCHAR(50),
    start_date TIMESTAMP,
    recovery_status VARCHAR(100),
    muscle_groups_to_avoid TEXT,
    goals_for_injury TEXT,
    notes TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create Workout Plan table
CREATE TABLE IF NOT EXISTS workout_plan (
    plan_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    plan_name VARCHAR(255),
    duration_minutes INTEGER,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_completed BOOLEAN DEFAULT FALSE,
    feedback_rating INTEGER,
    notes TEXT,
    injury_focus TEXT,
    equipment_used TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create Plan Exercise table
CREATE TABLE IF NOT EXISTS plan_exercise (
    plan_exercise_id VARCHAR(36) PRIMARY KEY,
    plan_id VARCHAR(36) NOT NULL,
    exercise_id VARCHAR(36),
    exercise_order INTEGER,
    sets INTEGER,
    reps INTEGER,
    duration_seconds INTEGER,
    form_tips TEXT,
    common_errors TEXT,
    FOREIGN KEY (plan_id) REFERENCES workout_plan(plan_id),
    FOREIGN KEY (exercise_id) REFERENCES exercise(exercise_id)
);

-- Create Workout Log table
CREATE TABLE IF NOT EXISTS workout_log (
    log_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    plan_id VARCHAR(36),
    workout_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration_minutes INTEGER,
    targeted_areas TEXT,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (plan_id) REFERENCES workout_plan(plan_id)
);

-- Create Chatbot Interaction table
CREATE TABLE IF NOT EXISTS chatbot_interaction (
    interaction_id SERIAL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_message TEXT,
    ai_response TEXT,
    related_exercise_id VARCHAR(36),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (related_exercise_id) REFERENCES exercise(exercise_id)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_user_username ON users (username);
CREATE INDEX IF NOT EXISTS idx_user_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_user_role ON users (role); 