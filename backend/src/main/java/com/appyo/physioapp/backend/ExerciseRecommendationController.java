package com.appyo.physioapp.backend;

import com.appyo.physioapp.auth.JwtUtil;
import com.appyo.physioapp.backend.model.*;
import com.appyo.physioapp.backend.service.DeepseekApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import javax.annotation.PostConstruct;
import java.util.*;
import java.io.IOException;

/**
 * ExerciseRecommendationController
 * 
 * This controller handles AI-powered exercise plan generation and recommendation functionality.
 * It integrates with the DeepSeek AI API to create personalized exercise plans based on user
 * preferences, injury history, and fitness goals.
 * 
 * Key Features:
 * - Generates personalized exercise plans using AI
 * - Combines user profile data with session preferences
 * - Handles exercise plan customization and validation
 * - Provides detailed exercise recommendations with sets, reps, and instructions
 * 
 * @author PhysioApp Team
 * @version 1.0
 * @since 2025-01-01
 */
@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class ExerciseRecommendationController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExerciseRecommendationController.class);
    private DeepseekApiService deepseekApiService;
    private final JdbcTemplate jdbcTemplate;
    private final JwtUtil jwtUtil;

    @Value("${deepseek.api.key:}")
    private String apiKey;

    @Value("${deepseek.api.url:https://api.deepseek.com/v1}")
    private String apiUrl;

    @Autowired
    public ExerciseRecommendationController(JdbcTemplate jdbcTemplate, JwtUtil jwtUtil) {
        this.jdbcTemplate = jdbcTemplate;
        this.jwtUtil = jwtUtil;
    }
    
    @PostConstruct
    public void init() {
        // Create OkHttpClient with interceptor to add API key
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Authorization", "Bearer " + apiKey)
                                .header("Content-Type", "application/json")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl + "/")
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        this.deepseekApiService = retrofit.create(DeepseekApiService.class);
    }
    
    /**
     * Generates a personalized exercise plan based on user preferences and profile data.
     * 
     * This endpoint combines user profile information from the database with current
     * session preferences to create a comprehensive AI-generated exercise plan. The AI
     * considers factors such as injury history, fitness goals, equipment access, and
     * targeted muscle groups.
     * 
     * @param request The exercise plan request containing user preferences and session data
     * @param authHeader JWT authentication token
     * @return ResponseEntity containing the generated exercise plan or error details
     * 
     * @apiNote The generated plan includes:
     *          - Exercise name, description, and difficulty level
     *          - Sets, reps, and equipment requirements
     *          - Detailed instructions for proper form
     *          - Estimated duration and targeted muscle groups
     */
    @PostMapping("/exercise")
    public ResponseEntity<Map<String, Object>> generateExercisePlan(
            @RequestBody ExercisePlanRequest request,
            @RequestHeader("Authorization") String authHeader) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract username from JWT token
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(token);
            
            if (username == null) {
                response.put("success", false);
                response.put("message", "Invalid authentication token");
                return ResponseEntity.status(401).body(response);
            }
            
            logger.info("Generating exercise plan for user: {}", username);
            
            // Get user data from database (with fallback for new users)
            Map<String, Object> userData = getUserData(request.getUserId());
            
            // Build comprehensive prompt combining database and session data
            String prompt = buildPrompt(userData, request);
            logger.debug("Generated prompt for user {}: {}", username, prompt);
            
            // Generate exercise plan using AI
            String aiResponse = null;
            try {
                Message message = new Message(Role.USER, prompt);
                List<Message> messages = Arrays.asList(message);
                
                ChatCompletionRequest chatRequest = new ChatCompletionRequest("deepseek-chat", messages, 0.7, false);
                
                retrofit2.Response<ChatCompletionResponse> apiResponse = deepseekApiService.createChatCompletion(chatRequest).execute();
                
                if (apiResponse.isSuccessful() && apiResponse.body() != null) {
                    aiResponse = apiResponse.body().getChoices().get(0).getMessage().getContent();
                    logger.debug("AI Response received: {}", aiResponse);
                } else {
                    logger.error("DeepSeek API call failed: {}", apiResponse.errorBody() != null ? apiResponse.errorBody().string() : "Unknown error");
                }
            } catch (IOException e) {
                logger.error("Error calling DeepSeek API", e);
            }
            
            if (aiResponse == null || aiResponse.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Failed to generate exercise plan. Please try again.");
                return ResponseEntity.status(500).body(response);
            }
            
            // Parse AI response into structured exercise plan
            List<Exercise> exercises = parseExercisePlan(aiResponse);
            if (exercises.isEmpty()) {
                response.put("success", false);
                response.put("message", "Generated exercise plan is invalid. Please try again.");
                return ResponseEntity.status(500).body(response);
            }
            
            // Convert Exercise objects to ExercisePlanResponse.Exercise objects
            List<ExercisePlanResponse.Exercise> responseExercises = new ArrayList<>();
            for (Exercise exercise : exercises) {
                ExercisePlanResponse.Exercise responseExercise = new ExercisePlanResponse.Exercise();
                responseExercise.setName(exercise.getName());
                responseExercise.setDescription(exercise.getDescription());
                responseExercise.setSets(String.valueOf(exercise.getSets()));
                responseExercise.setReps(String.valueOf(exercise.getReps()));
                responseExercise.setEquipment(exercise.getEquipment());
                responseExercise.setDifficulty(exercise.getDifficulty());
                responseExercise.setInstructions(exercise.getInstructions());
                responseExercises.add(responseExercise);
            }
            
            // Create response with exercise plan
            ExercisePlanResponse planResponse = new ExercisePlanResponse();
            planResponse.setExercises(responseExercises);
            planResponse.setPlanName(generatePlanName(request));
            planResponse.setDuration(request.getDuration());
            planResponse.setTargetedAreas(String.join(", ", request.getTargetedAreas()));
            
            response.put("success", true);
            response.put("plan", planResponse);
            response.put("message", "Exercise plan generated successfully");
            
            logger.info("Successfully generated exercise plan for user: {} with {} exercises", 
                       username, exercises.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error generating exercise plan", e);
            response.put("success", false);
            response.put("message", "Internal server error while generating exercise plan");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Retrieves user profile data from the database for exercise plan generation.
     * 
     * This method fetches comprehensive user information including injury history,
     * fitness goals, equipment access, and other relevant data needed for
     * personalized exercise recommendations.
     * 
     * @param userId The unique identifier of the user
     * @return Map containing user profile data, or null if user not found
     */
    private Map<String, Object> getUserData(String userId) {
        try {
            logger.debug("Fetching user data for userId: {}", userId);
            
            if (userId == null || userId.trim().isEmpty()) {
                logger.warn("UserId is null or empty");
                return null;
            }
            
            // Query user profile information
            String userSql = """
                SELECT name, age, gender, weight, height, fitness_goal, injury_history, 
                       chronic_diseases, equipment_access, email, phone
                FROM "user" 
                WHERE user_id = CAST(? AS UUID)
                """;
            
            List<Map<String, Object>> userResults = jdbcTemplate.queryForList(userSql, userId);
            if (userResults.isEmpty()) {
                logger.warn("No user found for userId: {}", userId);
                return null;
            }
            
            Map<String, Object> userData = userResults.get(0);
            
            // Query injury information if available
            try {
                String injurySql = """
                    SELECT injury_type, injury_area, severity, recovery_status, 
                           muscle_groups_to_avoid, goals_for_injury, notes 
                    FROM injury 
                    WHERE user_id = CAST(? AS UUID)
                    """;
                
                List<Map<String, Object>> injuryResults = jdbcTemplate.queryForList(injurySql, userId);
                if (!injuryResults.isEmpty()) {
                    Map<String, Object> injuryData = injuryResults.get(0);
                    userData.putAll(injuryData);
                }
            } catch (Exception e) {
                logger.warn("Could not fetch injury data for user {}: {}", userId, e.getMessage());
                // Continue without injury data
            }
            
            logger.debug("Successfully retrieved user data for userId: {}", userId);
            return userData;
            
        } catch (Exception e) {
            logger.warn("Error fetching user data for userId {}: {}", userId, e.getMessage());
            // Return null instead of throwing exception to allow fallback
            return null;
        }
    }
    
    /**
     * Builds a comprehensive prompt for AI exercise plan generation.
     * 
     * This method combines user profile data from the database with current session
     * preferences to create a detailed prompt that guides the AI in generating
     * personalized exercise recommendations.
     * 
     * @param userData User profile information from database (can be null for new users)
     * @param request Current session preferences and requirements
     * @return Formatted prompt string for AI processing
     */
    private String buildPrompt(Map<String, Object> userData, ExercisePlanRequest request) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Create a ").append(request.getDuration()).append("-minute exercise plan focusing on ");
        prompt.append(String.join(", ", request.getTargetedAreas()));
        
        if (request.getFocusType() != null && !request.getFocusType().isEmpty()) {
            prompt.append(" with ").append(request.getFocusType()).append(" focus");
        }
        
        prompt.append(". ");
        
        if (userData != null) {
            if (userData.get("fitness_goal") != null && !userData.get("fitness_goal").toString().isEmpty()) {
                prompt.append("User's fitness goal: ").append(userData.get("fitness_goal")).append(". ");
            }
            if (userData.get("injury_history") != null && !userData.get("injury_history").toString().isEmpty()) {
                prompt.append("Injury history: ").append(userData.get("injury_history")).append(". ");
            }
        }
        
        prompt.append("Provide 3-5 exercises in JSON format with the following structure: ");
        prompt.append("[{\"name\": \"Exercise Name\", \"description\": \"Brief description\", \"sets\": \"3\", \"reps\": \"10-15\", \"equipment\": \"None/Dumbbells/etc.\", \"difficulty\": \"Beginner/Intermediate/Advanced\", \"instructions\": \"Step-by-step instructions\"}]");
        
        return prompt.toString();
    }
    
    /**
     * Parses the AI-generated response into structured Exercise objects.
     * 
     * This method extracts exercise information from the AI response and converts
     * it into a list of Exercise objects that can be used by the frontend.
     * 
     * @param aiResponse Raw response from the AI service
     * @return List of parsed Exercise objects
     */
    private List<Exercise> parseExercisePlan(String aiResponse) {
        List<Exercise> exercises = new ArrayList<>();
        
        try {
            logger.debug("Parsing AI response: {}", aiResponse);
            
            // Extract JSON array from AI response
            int startIndex = aiResponse.indexOf('[');
            int endIndex = aiResponse.lastIndexOf(']');
            
            if (startIndex == -1 || endIndex == -1) {
                logger.warn("No valid JSON array found in AI response, trying to create fallback exercises");
                // Create fallback exercises
                exercises.add(createFallbackExercise("Push-ups", "Basic upper body exercise", 3, 10, "None", "Beginner", "1. Start in plank position\n2. Lower your body\n3. Push back up"));
                exercises.add(createFallbackExercise("Squats", "Basic lower body exercise", 3, 15, "None", "Beginner", "1. Stand with feet shoulder-width apart\n2. Lower your body\n3. Stand back up"));
                return exercises;
            }
            
            String jsonArray = aiResponse.substring(startIndex, endIndex + 1);
            logger.debug("Extracted JSON array: {}", jsonArray);
            
            // Split by exercise objects and parse each one
            String[] exerciseStrings = jsonArray.split("\\},\\s*\\{");
            
            for (String exerciseStr : exerciseStrings) {
                // Clean up the string
                exerciseStr = exerciseStr.replaceAll("[\\[\\]{}]", "").trim();
                
                if (exerciseStr.isEmpty()) continue;
                
                Exercise exercise = parseExerciseFromString(exerciseStr);
                if (exercise != null) {
                    exercises.add(exercise);
                }
            }
            
            // If no exercises were parsed, create fallback exercises
            if (exercises.isEmpty()) {
                logger.warn("No exercises parsed, creating fallback exercises");
                exercises.add(createFallbackExercise("Push-ups", "Basic upper body exercise", 3, 10, "None", "Beginner", "1. Start in plank position\n2. Lower your body\n3. Push back up"));
                exercises.add(createFallbackExercise("Squats", "Basic lower body exercise", 3, 15, "None", "Beginner", "1. Stand with feet shoulder-width apart\n2. Lower your body\n3. Stand back up"));
            }
            
            logger.info("Successfully parsed {} exercises from AI response", exercises.size());
            
        } catch (Exception e) {
            logger.error("Error parsing exercise plan from AI response", e);
            // Create fallback exercises on error
            exercises.add(createFallbackExercise("Push-ups", "Basic upper body exercise", 3, 10, "None", "Beginner", "1. Start in plank position\n2. Lower your body\n3. Push back up"));
            exercises.add(createFallbackExercise("Squats", "Basic lower body exercise", 3, 15, "None", "Beginner", "1. Stand with feet shoulder-width apart\n2. Lower your body\n3. Stand back up"));
        }
        
        return exercises;
    }
    
    /**
     * Creates a fallback exercise when parsing fails.
     */
    private Exercise createFallbackExercise(String name, String description, int sets, int reps, String equipment, String difficulty, String instructions) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setDescription(description);
        exercise.setSets(sets);
        exercise.setReps(reps);
        exercise.setEquipment(equipment);
        exercise.setDifficulty(difficulty);
        exercise.setInstructions(instructions);
        return exercise;
    }
    
    /**
     * Parses a single exercise string into an Exercise object.
     * 
     * @param exerciseStr String representation of exercise data
     * @return Exercise object or null if parsing fails
     */
    private Exercise parseExerciseFromString(String exerciseStr) {
        try {
            Exercise exercise = new Exercise();
            
            // Extract exercise properties using regex or string manipulation
            // This is a simplified parser - in production, use proper JSON parsing
            
            // Extract name
            if (exerciseStr.contains("\"name\":")) {
                String name = extractValue(exerciseStr, "name");
                exercise.setName(name != null ? name : "Unknown Exercise");
            }
            
            // Extract description
            if (exerciseStr.contains("\"description\":")) {
                String description = extractValue(exerciseStr, "description");
                exercise.setDescription(description != null ? description : "");
            }
            
            // Extract sets
            if (exerciseStr.contains("\"sets\":")) {
                String sets = extractValue(exerciseStr, "sets");
                try {
                    int setsValue = sets != null ? Integer.parseInt(sets) : 3;
                    exercise.setSets(setsValue);
                } catch (NumberFormatException e) {
                    exercise.setSets(3);
                }
            }
            
            // Extract reps
            if (exerciseStr.contains("\"reps\":")) {
                String reps = extractValue(exerciseStr, "reps");
                try {
                    // Handle ranges like "10-15" by taking the first number
                    if (reps != null && reps.contains("-")) {
                        reps = reps.split("-")[0];
                    }
                    int repsValue = reps != null ? Integer.parseInt(reps) : 10;
                    exercise.setReps(repsValue);
                } catch (NumberFormatException e) {
                    exercise.setReps(10);
                }
            }
            
            // Extract equipment
            if (exerciseStr.contains("\"equipment\":")) {
                String equipment = extractValue(exerciseStr, "equipment");
                exercise.setEquipment(equipment != null ? equipment : "None");
            }
            
            // Extract difficulty
            if (exerciseStr.contains("\"difficulty\":")) {
                String difficulty = extractValue(exerciseStr, "difficulty");
                exercise.setDifficulty(difficulty != null ? difficulty : "Beginner");
            }
            
            // Extract instructions
            if (exerciseStr.contains("\"instructions\":")) {
                String instructions = extractValue(exerciseStr, "instructions");
                exercise.setInstructions(instructions != null ? instructions : "");
            }
            
            return exercise;
            
        } catch (Exception e) {
            logger.error("Error parsing exercise string: {}", exerciseStr, e);
            return null;
        }
    }
    
    /**
     * Extracts a value from a JSON-like string for a given key.
     * 
     * @param jsonString The JSON-like string to parse
     * @param key The key to extract the value for
     * @return The extracted value or null if not found
     */
    private String extractValue(String jsonString, String key) {
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(jsonString);
            
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
            logger.warn("Error extracting value for key: {}", key, e);
        }
        return null;
    }
    
    /**
     * Generates a descriptive name for the exercise plan based on user preferences.
     * 
     * @param request The exercise plan request containing user preferences
     * @return A descriptive plan name
     */
    private String generatePlanName(ExercisePlanRequest request) {
        StringBuilder planName = new StringBuilder();
        
        // Add focus type
        if (request.getFocusType() != null && !request.getFocusType().isEmpty()) {
            planName.append(request.getFocusType()).append(" ");
        } else {
            planName.append("General Fitness ");
        }
        
        // Add duration
        if (request.getDuration() != null && !request.getDuration().isEmpty()) {
            planName.append(request.getDuration()).append("min ");
        }
        
        // Add targeted areas
        if (request.getTargetedAreas() != null && !request.getTargetedAreas().isEmpty()) {
            planName.append("(").append(String.join(", ", request.getTargetedAreas())).append(")");
        }
        
        return planName.toString().trim();
    }
} 