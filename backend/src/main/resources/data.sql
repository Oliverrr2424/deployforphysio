-- Insert test users
INSERT INTO users (user_id, username, email, password_hash, name, gender, age, phone, height, weight, chronic_diseases, injury_history, fitness_goal, equipment_access, role, created_at) VALUES
('11111111-1111-1111-1111-111111111111', 'testuser1', 'test1@example.com', '$2a$10$hash1', 'John Doe', 'Male', 30, '+1234567890', 175.0, 75.0, NULL, 'Previous ankle sprain in 2020', 'Improve overall strength and flexibility', 'Home gym with basic equipment', 'MEMBER', CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222222', 'testuser2', 'test2@example.com', '$2a$10$hash2', 'Jane Smith', 'Female', 28, '+1234567891', 165.0, 60.0, 'Mild asthma', 'Lower back injury in 2021', 'Weight loss and cardio improvement', 'Full gym access', 'MEMBER', CURRENT_TIMESTAMP),
('33333333-3333-3333-3333-333333333333', 'testuser3', 'test3@example.com', '$2a$10$hash3', 'Mike Johnson', 'Male', 35, '+1234567892', 180.0, 85.0, 'Type 2 diabetes', NULL, 'Muscle gain and strength training', 'Bodyweight only', 'MEMBER', CURRENT_TIMESTAMP),
('44444444-4444-4444-4444-444444444444', 'physio1', 'physio@example.com', '$2a$10$hash4', 'Dr. Sarah Wilson', 'Female', 40, '+1234567893', 170.0, 65.0, NULL, NULL, 'Professional development', 'Professional equipment', 'PHYSIOTHERAPIST', CURRENT_TIMESTAMP);

-- Insert test injuries
INSERT INTO injury (user_id, injury_type, injury_area, severity, start_date, recovery_status, muscle_groups_to_avoid, goals_for_injury, notes) VALUES
('11111111-1111-1111-1111-111111111111', 'Lower Back Pain', 'Lumbar spine', 'Minor', '2024-01-15', 'Improving', 'Lower back extensors', 'Strengthen core and improve posture', 'Occasional discomfort during long sitting periods'),
('11111111-1111-1111-1111-111111111111', 'Knee Strain', 'Right knee', 'Mild', '2024-03-10', 'Recovered', 'Quadriceps under heavy load', 'Maintain knee stability', 'Fully recovered but needs to avoid high impact'),
('22222222-2222-2222-2222-222222222222', 'Shoulder Impingement', 'Left shoulder', 'Moderate', '2024-02-20', 'In Progress', 'Overhead movements', 'Restore full range of motion', 'Limited overhead reach, improving with PT'),
('33333333-3333-3333-3333-333333333333', 'Wrist Tendinitis', 'Both wrists', 'Mild', '2024-04-01', 'Stable', 'Heavy grip work', 'Prevent further aggravation', 'Manageable with proper warm-up and technique');

-- Insert test exercises
INSERT INTO exercise (exercise_id, name, description, target_muscles, secondary_muscles, equipment_required, difficulty_level, image_url, video_url) VALUES
('11111111-1111-1111-1111-111111111111', 'Bodyweight Squat', 'Basic squatting movement using body weight', 'Quadriceps, Glutes', 'Hamstrings, Calves, Core', 'None', 'Beginner', NULL, NULL),
('22222222-2222-2222-2222-222222222222', 'Push-up', 'Classic upper body pushing exercise', 'Chest, Triceps', 'Shoulders, Core', 'None', 'Beginner', NULL, NULL),
('33333333-3333-3333-3333-333333333333', 'Plank', 'Isometric core strengthening exercise', 'Core', 'Shoulders, Glutes', 'None', 'Beginner', NULL, NULL),
('44444444-4444-4444-4444-444444444444', 'Dumbbell Row', 'Pulling exercise for back muscles', 'Latissimus Dorsi, Rhomboids', 'Biceps, Rear Deltoids', 'Dumbbells', 'Intermediate', NULL, NULL),
('55555555-5555-5555-5555-555555555555', 'Goblet Squat', 'Squat variation with weight held at chest', 'Quadriceps, Glutes', 'Hamstrings, Core', 'Dumbbell or Kettlebell', 'Intermediate', NULL, NULL);

-- Insert test workout plans
INSERT INTO workout_plan (plan_id, user_id, plan_name, duration_minutes, generated_at, is_completed, feedback_rating, notes, injury_focus, equipment_used) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'Lower Back Recovery Plan', 45, CURRENT_TIMESTAMP, FALSE, NULL, 'Focus on core strengthening and back mobility', 'Lower back pain', 'Bodyweight exercises'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 'Shoulder Rehabilitation Program', 60, CURRENT_TIMESTAMP, FALSE, NULL, 'Gradual shoulder mobility and strength', 'Shoulder impingement', 'Resistance bands, light weights');

-- Insert test plan exercises
INSERT INTO plan_exercise (plan_exercise_id, plan_id, exercise_id, exercise_order, sets, reps, duration_seconds, form_tips, common_errors) VALUES
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 1, 3, 12, NULL, 'Keep chest up and knees behind toes', 'Knee valgus, forward lean'),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '33333333-3333-3333-3333-333333333333', 2, 3, NULL, 30, 'Maintain straight line from head to heels', 'Sagging hips, head position'); 