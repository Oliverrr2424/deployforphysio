package com.appyo.physioapp.backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import com.appyo.physioapp.auth.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/manual-exercise")
@CrossOrigin(origins = "*")
public class ManualExerciseController {

    private static final Logger logger = LoggerFactory.getLogger(ManualExerciseController.class);
    private final JdbcTemplate jdbcTemplate;
    private final JwtUtil jwtUtil;

    public ManualExerciseController(JdbcTemplate jdbcTemplate, JwtUtil jwtUtil) {
        this.jdbcTemplate = jdbcTemplate;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Adds a manual exercise entry based on the scheduled date.
     * 
     * Behavior varies by date:
     * - Past date: Stores directly to database as completed
     * - Today: Creates planned exercise and optionally starts workout session
     * - Future date: Creates planned exercise for future execution
     * 
     * @param request Exercise data including date, exercise details, and completion info
     * @param authHeader JWT authentication token
     * @return ResponseEntity with result and next action
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addManualExercise(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate JWT token
            String token = authHeader.replace("Bearer ", "");
            if (!jwtUtil.validateToken(token)) {
                response.put("success", false);
                response.put("message", "Invalid authentication token");
                return ResponseEntity.status(401).body(response);
            }
            
            String username = jwtUtil.extractUsername(token);
            String userId = (String) request.get("userId");
            String exerciseName = (String) request.get("exerciseName");
            String scheduledDateStr = (String) request.get("scheduledDate");
            Integer plannedSets = (Integer) request.getOrDefault("plannedSets", 3);
            String plannedReps = (String) request.getOrDefault("plannedReps", "10-15");
            Double plannedWeight = ((Number) request.getOrDefault("plannedWeight", 0.0)).doubleValue();
            Integer plannedDuration = (Integer) request.getOrDefault("plannedDuration", 300); // 5 minutes default
            String notes = (String) request.getOrDefault("notes", "");
            
            // Parse and validate date
            LocalDate scheduledDate = LocalDate.parse(scheduledDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate today = LocalDate.now();
            
            logger.info("Adding manual exercise '{}' for user: {} on date: {}", exerciseName, username, scheduledDate);
            
            if (scheduledDate.isBefore(today)) {
                // Past date - store as completed exercise session
                return handlePastExercise(userId, exerciseName, scheduledDate, plannedSets, plannedReps, 
                                        plannedWeight, plannedDuration, notes, response);
                                        
            } else if (scheduledDate.isEqual(today)) {
                // Today - create planned exercise and offer to start workout
                return handleTodayExercise(userId, exerciseName, scheduledDate, plannedSets, plannedReps, 
                                         plannedWeight, plannedDuration, notes, response);
                                         
            } else {
                // Future date - store as planned exercise
                return handleFutureExercise(userId, exerciseName, scheduledDate, plannedSets, plannedReps, 
                                          plannedWeight, plannedDuration, notes, response);
            }
            
        } catch (Exception e) {
            logger.error("Error adding manual exercise", e);
            response.put("success", false);
            response.put("message", "Error adding exercise: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    private ResponseEntity<Map<String, Object>> handlePastExercise(
            String userId, String exerciseName, LocalDate scheduledDate, 
            Integer plannedSets, String plannedReps, Double plannedWeight, 
            Integer plannedDuration, String notes, Map<String, Object> response) {
        
        try {
            // Insert directly into exercise_sessions as completed
            String sql = """
                INSERT INTO exercise_sessions (user_id, exercise_name, sets, reps, weight, duration_seconds,
                                             session_date, scheduled_date, notes, completed, plan_name, 
                                             exercise_order, status)
                VALUES (CAST(? AS UUID), ?, ?, ?, ?, ?, ?, ?, ?, true, 'Manual Entry', 1, 'completed')
                """;
            
            jdbcTemplate.update(sql, userId, exerciseName, plannedSets, plannedReps, plannedWeight, 
                              plannedDuration, scheduledDate, scheduledDate, notes);
            
            // Also create a workout session record
            String workoutSql = """
                INSERT INTO workout_sessions (user_id, session_name, session_date, start_time, end_time,
                                            total_duration_seconds, exercises_completed, exercises_planned, completed)
                VALUES (CAST(? AS UUID), ?, ?, ?::timestamp, ?::timestamp, ?, 1, 1, true)
                """;
            
            String sessionName = "Manual Entry - " + scheduledDate;
            String timestamp = scheduledDate + "T12:00:00";
            String endTimestamp = scheduledDate + "T12:" + String.format("%02d", plannedDuration / 60) + ":00";
            
            jdbcTemplate.update(workoutSql, userId, sessionName, scheduledDate, timestamp, endTimestamp, plannedDuration);
            
            response.put("success", true);
            response.put("message", "Past exercise logged successfully");
            response.put("action", "stored");
            response.put("date", scheduledDate.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error handling past exercise", e);
            response.put("success", false);
            response.put("message", "Error logging past exercise: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    private ResponseEntity<Map<String, Object>> handleTodayExercise(
            String userId, String exerciseName, LocalDate scheduledDate,
            Integer plannedSets, String plannedReps, Double plannedWeight, 
            Integer plannedDuration, String notes, Map<String, Object> response) {
        
        try {
            // Insert into planned_exercises table
            String sql = """
                INSERT INTO planned_exercises (user_id, exercise_name, planned_sets, planned_reps, 
                                             planned_weight, planned_duration_seconds, scheduled_date, 
                                             notes, plan_name, exercise_order, status)
                VALUES (CAST(? AS UUID), ?, ?, ?, ?, ?, ?, ?, 'Manual Entry', 1, 'planned')
                RETURNING plan_id
                """;
            
            String planId = jdbcTemplate.queryForObject(sql, String.class, userId, exerciseName, 
                                                       plannedSets, plannedReps, plannedWeight, 
                                                       plannedDuration, scheduledDate, notes);
            
            response.put("success", true);
            response.put("message", "Exercise scheduled for today");
            response.put("action", "start_workout");
            response.put("planId", planId);
            response.put("date", scheduledDate.toString());
            response.put("exerciseDetails", Map.of(
                "name", exerciseName,
                "sets", plannedSets.toString(),
                "reps", plannedReps,
                "weight", plannedWeight,
                "duration", plannedDuration,
                "equipment", "As needed",
                "difficulty", "Moderate",
                "description", "Manual exercise entry",
                "instructions", notes.isEmpty() ? "Perform as planned" : notes
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error handling today's exercise", e);
            response.put("success", false);
            response.put("message", "Error scheduling today's exercise: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    private ResponseEntity<Map<String, Object>> handleFutureExercise(
            String userId, String exerciseName, LocalDate scheduledDate,
            Integer plannedSets, String plannedReps, Double plannedWeight, 
            Integer plannedDuration, String notes, Map<String, Object> response) {
        
        try {
            // Insert into planned_exercises table
            String sql = """
                INSERT INTO planned_exercises (user_id, exercise_name, planned_sets, planned_reps, 
                                             planned_weight, planned_duration_seconds, scheduled_date, 
                                             notes, plan_name, exercise_order, status)
                VALUES (CAST(? AS UUID), ?, ?, ?, ?, ?, ?, ?, 'Manual Entry', 1, 'planned')
                """;
            
            jdbcTemplate.update(sql, userId, exerciseName, plannedSets, plannedReps, plannedWeight, 
                              plannedDuration, scheduledDate, notes);
            
            response.put("success", true);
            response.put("message", "Exercise scheduled for " + scheduledDate);
            response.put("action", "scheduled");
            response.put("date", scheduledDate.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error handling future exercise", e);
            response.put("success", false);
            response.put("message", "Error scheduling future exercise: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
} 