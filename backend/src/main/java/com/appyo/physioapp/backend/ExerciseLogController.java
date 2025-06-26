package com.appyo.physioapp.backend;

import com.appyo.physioapp.auth.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ExerciseLogController
 * 
 * This controller manages exercise session logging and retrieval functionality.
 * It handles the storage and retrieval of completed workout sessions, including
 * exercise details, duration, and user performance metrics.
 * 
 * Key Features:
 * - Stores completed exercise sessions with detailed exercise information
 * - Retrieves exercise logs by user and date range
 * - Tracks exercise completion status (completed vs skipped)
 * - Provides workout session analytics and progress tracking
 * 
 * Database Tables Used:
 * - workout_log: Stores session metadata and exercise summaries
 * - workout_plan: References for plan-based workouts (optional)
 * 
 * @author PhysioApp Team
 * @version 1.0
 * @since 2025-01-01
 */
@RestController
@RequestMapping("/api/exercise-logs")
@CrossOrigin(origins = "*")
public class ExerciseLogController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExerciseLogController.class);
    private final JdbcTemplate jdbcTemplate;
    private final JwtUtil jwtUtil;
    
    public ExerciseLogController(JdbcTemplate jdbcTemplate, JwtUtil jwtUtil) {
        this.jdbcTemplate = jdbcTemplate;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * Logs a completed exercise session to the database.
     * 
     * This endpoint processes exercise session data from the frontend and stores
     * it in the exercise_sessions table. It parses individual exercise completion
     * data and creates detailed exercise records including:
     * - Individual exercise entries with sets/reps data
     * - Session metadata (start/end times, duration)
     * - Exercise completion details (sets, reps, skip status)
     * - Targeted muscle groups and session notes
     * 
     * @param authHeader JWT authentication token for user verification
     * @param sessionData Map containing session information and exercise details
     * @return ResponseEntity with success status and confirmation message
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> logExerciseSession(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> sessionData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate JWT token for authentication
            String token = authHeader.replace("Bearer ", "");
            if (!jwtUtil.validateToken(token)) {
                response.put("success", false);
                response.put("message", "Invalid authentication token");
                return ResponseEntity.status(401).body(response);
            }
            
            String username = jwtUtil.extractUsername(token);
            logger.info("Logging exercise session for user: {}", username);
            
            // Extract and validate session data
            String userId = sessionData.get("userId").toString();
            String sessionStartTime = (String) sessionData.get("sessionStartTime");
            String sessionEndTime = (String) sessionData.get("sessionEndTime");
            Integer totalDuration = (Integer) sessionData.get("totalDuration");
            String status = sessionData.getOrDefault("status", "completed").toString();
            
            // Validate required fields
            if (userId == null || sessionStartTime == null || sessionEndTime == null || totalDuration == null) {
                response.put("success", false);
                response.put("message", "Missing required session data");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Process exercise list
            List<Map<String, Object>> exercises = (List<Map<String, Object>>) sessionData.get("exercises");
            if (exercises == null || exercises.isEmpty()) {
                response.put("success", false);
                response.put("message", "No exercises provided in session data");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create a workout session record first
            String sessionName = "Workout Session - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String workoutSessionSql = """
                INSERT INTO workout_sessions (user_id, session_name, session_date, start_time, end_time, 
                                            total_duration_seconds, exercises_completed, exercises_planned, completed)
                VALUES (CAST(? AS UUID), ?, CURRENT_DATE, ?::timestamp, ?::timestamp, ?, ?, ?, ?)
                RETURNING workout_id
                """;
            
            String workoutId = jdbcTemplate.queryForObject(workoutSessionSql, String.class,
                userId, sessionName, sessionStartTime, sessionEndTime, totalDuration, 
                exercises.size(), exercises.size(), true);
            
            // Insert individual exercise records  
            String exerciseSql = """
                INSERT INTO exercise_sessions (user_id, exercise_name, sets, reps, weight, duration_seconds,
                                             session_date, notes, completed, plan_name, exercise_order, status, scheduled_date)
                VALUES (CAST(? AS UUID), ?, ?, ?, ?, ?, CURRENT_DATE, ?, ?, ?, ?, ?, CURRENT_DATE)
                """;
            
            int exerciseOrder = 1;
            int completedExercises = 0;
            Set<String> targetedAreas = new HashSet<>();
            StringBuilder sessionNotes = new StringBuilder();
            
            for (Map<String, Object> exercise : exercises) {
                String exerciseName = (String) exercise.get("exerciseName");
                Integer setsCompleted = (Integer) exercise.get("setsCompleted");
                Integer totalSets = (Integer) exercise.get("totalSets");
                
                // Parse reps - handle both integer and string formats like "10-15"
                Object repsObj = exercise.get("reps");
                Integer reps = 0;
                String repsDisplay = "0";
                
                if (repsObj instanceof Integer) {
                    reps = (Integer) repsObj;
                    repsDisplay = reps.toString();
                } else if (repsObj instanceof String) {
                    repsDisplay = (String) repsObj;
                    // If it contains a range like "10-15", take the first number for DB storage
                    if (repsDisplay.contains("-")) {
                        reps = Integer.parseInt(repsDisplay.split("-")[0]);
                    } else {
                        reps = Integer.parseInt(repsDisplay);
                    }
                }
                
                Boolean skipped = (Boolean) exercise.get("skipped");
                
                // Parse weight if available (default to 0)
                Double weight = 0.0;
                
                // Calculate duration per exercise (rough estimate)
                int exerciseDuration = totalDuration / exercises.size();
                
                // Build exercise notes
                String exerciseNotes = String.format("%s: %d/%d sets, %s reps%s", 
                    exerciseName, setsCompleted, totalSets, repsDisplay, 
                    skipped ? " (skipped)" : "");
                
                if (sessionNotes.length() > 0) {
                    sessionNotes.append("; ");
                }
                sessionNotes.append(exerciseNotes);
                
                // Add targeted areas
                addTargetedAreas(exerciseName, targetedAreas);
                
                // Determine exercise status based on completion and session status
                String exerciseStatus = skipped ? "skipped" : 
                                       (status.equals("partial") && setsCompleted == 0) ? "planned" : 
                                       "completed";
                
                // Insert exercise record
                jdbcTemplate.update(exerciseSql,
                    userId, exerciseName, setsCompleted, reps, weight, exerciseDuration,
                    exerciseNotes, !skipped, sessionName, exerciseOrder, exerciseStatus);
                
                if (!skipped && setsCompleted > 0) {
                    completedExercises++;
                }
                
                exerciseOrder++;
            }
            
            // Update workout session with final counts
            String updateSessionSql = """
                UPDATE workout_sessions 
                SET exercises_completed = ?, notes = ?
                WHERE workout_id = CAST(? AS UUID)
                """;
            
            jdbcTemplate.update(updateSessionSql, completedExercises, sessionNotes.toString(), workoutId);
            
            logger.info("Successfully logged exercise session for user: {} with {} exercises", 
                       username, exercises.size());
            
            response.put("success", true);
            response.put("message", "Exercise session logged successfully");
            response.put("sessionId", workoutId);
            response.put("exercisesLogged", exercises.size());
            response.put("exercisesCompleted", completedExercises);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error logging exercise session", e);
            response.put("success", false);
            response.put("message", "Error logging exercise session: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Retrieves recent exercise logs for a specific user.
     * 
     * This endpoint fetches exercise session history for a user within a specified
     * time range. It returns detailed session information including exercise
     * summaries, duration, and targeted muscle groups.
     * 
     * @param userId The unique identifier of the user
     * @param days Number of days to look back (default: 7)
     * @param authHeader JWT authentication token
     * @return ResponseEntity containing exercise logs or error details
     * 
     * @apiNote Returns logs in descending chronological order with the following structure:
     *          {
     *            "success": true,
     *            "logs": [
     *              {
     *                "log_id": "uuid",
     *                "workout_date": "2025-06-22T00:30:00.000+00:00",
     *                "start_time": "2025-06-22T00:30:00.000+00:00",
     *                "end_time": "2025-06-22T01:00:00.000+00:00",
     *                "duration_minutes": 30,
     *                "targeted_areas": "Chest, Legs, Core",
     *                "notes": "Push-ups: 3/3 sets, 10-15 reps; Squats: 2/3 sets, 15-20 reps",
     *                "plan_name": "AI Generated Plan"
     *              }
     *            ]
     *          }
     */
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<Map<String, Object>> getUserRecentLogs(
            @PathVariable String userId,
            @RequestParam(defaultValue = "7") int days,
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
            logger.info("Fetching recent exercise logs for user: {} (last {} days)", username, days);
            
            // Query workout_log table with plan information
            String selectSql = """
                SELECT wl.log_id, wl.workout_date, wl.start_time, wl.end_time, wl.duration_minutes, 
                       wl.targeted_areas, wl.notes, 
                       COALESCE(wp.plan_name, 'AI Generated Plan') as plan_name
                FROM workout_log wl
                LEFT JOIN workout_plan wp ON wl.plan_id = wp.plan_id
                WHERE wl.user_id = CAST(? AS UUID) 
                  AND wl.workout_date >= CURRENT_DATE - INTERVAL '%d days'
                ORDER BY wl.workout_date DESC, wl.start_time DESC
                """.formatted(days);
            
            List<Map<String, Object>> logs = jdbcTemplate.queryForList(selectSql, userId);
            
            logger.info("Found {} exercise logs for user: {}", logs.size(), username);
            
            response.put("success", true);
            response.put("logs", logs);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching exercise logs for user: " + userId, e);
            response.put("success", false);
            response.put("message", "Error fetching exercise logs: " + e.getMessage());
            response.put("logs", new ArrayList<>());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Builds a comprehensive session summary from exercise data.
     * 
     * This method processes the list of exercises in a session and creates:
     * - Detailed notes describing each exercise and completion status
     * - Targeted muscle groups based on exercise names
     * - Session analytics and observations
     * 
     * @param exercises List of exercise completion data
     * @return SessionSummary containing notes and targeted areas
     */
    private SessionSummary buildSessionSummary(List<Map<String, Object>> exercises) {
        StringBuilder notesBuilder = new StringBuilder();
        Set<String> targetedAreas = new HashSet<>();
        
        for (Map<String, Object> exercise : exercises) {
            String exerciseName = (String) exercise.get("exerciseName");
            Integer setsCompleted = (Integer) exercise.get("setsCompleted");
            Integer totalSets = (Integer) exercise.get("totalSets");
            String reps = (String) exercise.get("reps");
            Boolean skipped = (Boolean) exercise.get("skipped");
            
            // Build exercise notes
            if (notesBuilder.length() > 0) {
                notesBuilder.append("; ");
            }
            
            if (skipped != null && skipped) {
                notesBuilder.append(exerciseName).append(": SKIPPED");
            } else {
                notesBuilder.append(exerciseName).append(": ")
                          .append(setsCompleted).append("/").append(totalSets)
                          .append(" sets, ").append(reps).append(" reps");
            }
            
            // Determine targeted muscle groups based on exercise name
            addTargetedAreas(exerciseName, targetedAreas);
        }
        
        return new SessionSummary(
            notesBuilder.toString(),
            String.join(", ", targetedAreas)
        );
    }
    
    /**
     * Determines targeted muscle groups based on exercise name.
     * 
     * This method uses keyword matching to identify which muscle groups
     * are targeted by specific exercises. It's a simplified approach that
     * can be enhanced with more sophisticated exercise classification.
     * 
     * @param exerciseName The name of the exercise
     * @param targetedAreas Set to add identified muscle groups to
     */
    private void addTargetedAreas(String exerciseName, Set<String> targetedAreas) {
        String name = exerciseName.toLowerCase();
        
        // Upper body exercises
        if (name.contains("push") || name.contains("chest") || name.contains("bench")) {
            targetedAreas.add("Chest");
        }
        if (name.contains("pull") || name.contains("row") || name.contains("back")) {
            targetedAreas.add("Back");
        }
        if (name.contains("shoulder") || name.contains("press") || name.contains("deltoid")) {
            targetedAreas.add("Shoulders");
        }
        if (name.contains("bicep") || name.contains("curl")) {
            targetedAreas.add("Biceps");
        }
        if (name.contains("tricep") || name.contains("dip")) {
            targetedAreas.add("Triceps");
        }
        
        // Lower body exercises
        if (name.contains("squat") || name.contains("leg") || name.contains("thigh")) {
            targetedAreas.add("Legs");
        }
        if (name.contains("lunge") || name.contains("step")) {
            targetedAreas.add("Legs");
        }
        if (name.contains("calf") || name.contains("heel")) {
            targetedAreas.add("Calves");
        }
        
        // Core exercises
        if (name.contains("plank") || name.contains("core") || name.contains("ab") || 
            name.contains("crunch") || name.contains("sit-up")) {
            targetedAreas.add("Core");
        }
        
        // Full body exercises
        if (name.contains("burpee") || name.contains("mountain") || name.contains("jumping")) {
            targetedAreas.add("Full Body");
        }
        
        // If no specific areas identified, default to full body
        if (targetedAreas.isEmpty()) {
            targetedAreas.add("Full Body");
        }
    }
    
    /**
     * Internal class to hold session summary data.
     */
    private static class SessionSummary {
        private final String notes;
        private final String targetedAreas;
        
        public SessionSummary(String notes, String targetedAreas) {
            this.notes = notes;
            this.targetedAreas = targetedAreas;
        }
        
        public String getNotes() { return notes; }
        public String getTargetedAreas() { return targetedAreas; }
    }

    /**
     * Creates a new exercise session directly (without going through plan page).
     * Allows users to add exercise sessions for any date from the progress page.
     * 
     * @param request The exercise session creation request
     * @param authHeader JWT authentication token
     * @return ResponseEntity containing the created session or error details
     */
    @PostMapping("/session/direct")
    public ResponseEntity<Map<String, Object>> createDirectExerciseSession(
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
            String userId = request.get("userId").toString();
            String sessionDate = request.get("sessionDate").toString();
            String exerciseName = request.get("exerciseName").toString();
            Integer sets = Integer.parseInt(request.get("sets").toString());
            Integer reps = Integer.parseInt(request.get("reps").toString());
            Integer weight = request.get("weight") != null ? Integer.parseInt(request.get("weight").toString()) : null;
            String notes = request.get("notes") != null ? request.get("notes").toString() : "";
            
            // Insert the exercise session
            String sql = """
                INSERT INTO exercise_sessions (user_id, session_date, exercise_name, sets, reps, weight, notes, created_at)
                VALUES (CAST(? AS UUID), ?, ?, ?, ?, ?, ?, NOW())
                """;
            
            jdbcTemplate.update(sql, userId, sessionDate, exerciseName, sets, reps, weight, notes);
            
            response.put("success", true);
            response.put("message", "Exercise session created successfully");
            response.put("sessionDate", sessionDate);
            response.put("exerciseName", exerciseName);
            
            logger.info("Direct exercise session created for user: {} on date: {}", username, sessionDate);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error creating direct exercise session", e);
            response.put("success", false);
            response.put("message", "Failed to create exercise session");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Retrieves exercise sessions for a specific date range (for calendar view).
     * 
     * @param userId The user ID
     * @param startDate Start date in YYYY-MM-DD format
     * @param endDate End date in YYYY-MM-DD format
     * @param authHeader JWT authentication token
     * @return ResponseEntity containing the sessions grouped by date
     */
    @GetMapping("/sessions/calendar")
    public ResponseEntity<Map<String, Object>> getSessionsForCalendar(
            @RequestParam String userId,
            @RequestParam String startDate,
            @RequestParam String endDate,
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
            
            // Query sessions for the date range
            String sql = """
                SELECT session_date, exercise_name, sets, reps, weight, notes, created_at
                FROM exercise_sessions 
                WHERE user_id = CAST(? AS UUID) 
                AND session_date BETWEEN CAST(? AS DATE) AND CAST(? AS DATE)
                ORDER BY session_date DESC, created_at DESC
                """;
            
            List<Map<String, Object>> sessions = jdbcTemplate.queryForList(sql, userId, startDate, endDate);
            
            // Group sessions by date
            Map<String, List<Map<String, Object>>> sessionsByDate = new HashMap<>();
            for (Map<String, Object> session : sessions) {
                String date = session.get("session_date").toString();
                sessionsByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(session);
            }
            
            response.put("success", true);
            response.put("sessions", sessionsByDate);
            response.put("totalSessions", sessions.size());
            
            logger.info("Retrieved {} sessions for user: {} from {} to {}", 
                       sessions.size(), username, startDate, endDate);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving sessions for calendar", e);
            response.put("success", false);
            response.put("message", "Failed to retrieve sessions");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
} 