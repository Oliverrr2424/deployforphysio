-- Clear existing data
TRUNCATE TABLE "public"."User" CASCADE;
TRUNCATE TABLE "public"."Exercise" CASCADE;
TRUNCATE TABLE "public"."injury" CASCADE;
TRUNCATE TABLE "public"."workout_plan" CASCADE;
TRUNCATE TABLE "public"."workout_log" CASCADE;
TRUNCATE TABLE "public"."plan_exercise" CASCADE;
TRUNCATE TABLE "public"."chatbot_interaction" CASCADE;

-- Insert test users
INSERT INTO "public"."User" (user_id, username, email, password_hash, created_at, last_login, gender, age, fitness_goal, equipment_access)
VALUES 
    ('11111111-1111-1111-1111-111111111111', 'testuser1', 'test1@example.com', 'hashed_password_1', NOW(), NOW(), 'male', 30, 'strength training', 'gym'),
    ('22222222-2222-2222-2222-222222222222', 'testuser2', 'test2@example.com', 'hashed_password_2', NOW(), NOW(), 'female', 25, 'weight loss', 'home'),
    ('33333333-3333-3333-3333-333333333333', 'testuser3', 'test3@example.com', 'hashed_password_3', NOW(), NOW(), 'male', 35, 'rehabilitation', 'gym');

-- Insert test exercises
INSERT INTO "public"."Exercise" (exercise_id, name, description, target_muscles, secondary_muscles, equipment_required, difficulty_level, image_url, video_url)
VALUES 
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'SQUAT', 'Basic squat exercise', 'quadriceps, glutes', 'core, hamstrings', 'none', 'Beginner', 'https://example.com/squat.jpg', 'https://example.com/squat.mp4'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'PUSH-UP', 'Standard push-up', 'chest, triceps', 'shoulders, core', 'none', 'Intermediate', 'https://example.com/pushup.jpg', 'https://example.com/pushup.mp4'),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'PLANK', 'Core stability exercise', 'core', 'shoulders, back', 'none', 'Beginner', 'https://example.com/plank.jpg', 'https://example.com/plank.mp4'),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'DEADLIFT', 'Compound lifting exercise', 'back, hamstrings', 'glutes, core', 'barbell', 'Advanced', 'https://example.com/deadlift.jpg', 'https://example.com/deadlift.mp4'),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'BENCH PRESS', 'Upper body strength exercise', 'chest, triceps', 'shoulders', 'bench, barbell', 'Intermediate', 'https://example.com/benchpress.jpg', 'https://example.com/benchpress.mp4');

-- Insert test injuries
INSERT INTO "public"."injury" (user_id, injury_type, injury_area, severity, start_date, recovery_status, muscle_groups_to_avoid, goals_for_injury, notes, updated_at)
VALUES 
    ('11111111-1111-1111-1111-111111111111', 'sprain', 'ankle', 'moderate', NOW() - INTERVAL '30 days', 'recovering', 'ankle, lower leg', 'regain full mobility', 'Avoid high-impact exercises', NOW()),
    ('22222222-2222-2222-2222-222222222222', 'strain', 'shoulder', 'mild', NOW() - INTERVAL '15 days', 'improving', 'shoulder, upper arm', 'strengthen rotator cuff', 'Focus on stability exercises', NOW()),
    ('33333333-3333-3333-3333-333333333333', 'tendinitis', 'knee', 'severe', NOW() - INTERVAL '45 days', 'recovering', 'knee, quadriceps', 'reduce inflammation', 'Avoid running and jumping', NOW());

-- Insert test workout plans
INSERT INTO "public"."workout_plan" (plan_id, user_id, plan_name, duration_minutes, generated_at, is_completed, feedback_rating, notes, injury_focus, equipment_used)
VALUES 
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'Ankle Recovery Program', 45, NOW() - INTERVAL '7 days', false, null, 'Focus on stability and mobility', 'ankle rehabilitation', 'resistance bands'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 'Shoulder Strengthening', 30, NOW() - INTERVAL '3 days', false, null, 'Emphasis on proper form', 'shoulder rehabilitation', 'dumbbells'),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '33333333-3333-3333-3333-333333333333', 'Knee Recovery Plan', 40, NOW() - INTERVAL '1 day', false, null, 'Low impact exercises only', 'knee rehabilitation', 'none');

-- Insert test workout logs
INSERT INTO "public"."workout_log" (log_id, user_id, plan_id, workout_date, start_time, end_time, duration_minutes, targeted_areas, notes)
VALUES 
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days' + INTERVAL '45 minutes', 45, 'ankle, lower leg', 'Good progress on balance exercises'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day' + INTERVAL '30 minutes', 30, 'shoulder, upper arm', 'Increased weight on shoulder press'),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '33333333-3333-3333-3333-333333333333', 'cccccccc-cccc-cccc-cccc-cccccccccccc', NOW(), NOW(), NOW() + INTERVAL '40 minutes', 40, 'knee, quadriceps', 'Started with basic mobility exercises');

-- Insert test plan exercises
INSERT INTO "public"."plan_exercise" (plan_exercise_id, plan_id, exercise_id, exercise_order, sets, reps, duration_seconds, form_tips, common_errors)
VALUES 
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 1, 3, 12, null, 'Keep back straight, feet shoulder-width apart', 'Knees caving inward'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 1, 3, 10, null, 'Maintain straight body line', 'Sagging hips'),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 1, 3, 30, 30, 'Keep core tight, body straight', 'Hips too high or low');

-- Insert test chatbot interactions
INSERT INTO "public"."chatbot_interaction" (user_id, timestamp, user_message, ai_response, related_exercise_id)
VALUES 
    ('11111111-1111-1111-1111-111111111111', NOW() - INTERVAL '1 day', 'What exercises are good for ankle recovery?', 'I recommend starting with ankle mobility exercises and gradually progressing to strength training.', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
    ('22222222-2222-2222-2222-222222222222', NOW() - INTERVAL '12 hours', 'How can I strengthen my shoulder?', 'Focus on rotator cuff exercises and gradually increase resistance.', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
    ('33333333-3333-3333-3333-333333333333', NOW() - INTERVAL '6 hours', 'What should I avoid with knee tendinitis?', 'Avoid high-impact activities and focus on low-impact exercises like swimming or cycling.', 'cccccccc-cccc-cccc-cccc-cccccccccccc'); 