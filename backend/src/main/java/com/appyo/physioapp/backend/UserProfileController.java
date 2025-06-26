package com.appyo.physioapp.backend;

import com.appyo.physioapp.auth.JwtUtil;
import com.appyo.physioapp.auth.UserRepository;
import com.appyo.physioapp.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * UserProfileController
 * 
 * This controller manages user profile operations including retrieval, updates,
 * and profile data management. It provides secure access to user information
 * and handles profile customization for personalized exercise recommendations.
 * 
 * Key Features:
 * - Retrieves complete user profile information
 * - Updates user profile fields with validation
 * - Handles null/empty field updates safely
 * - Provides JWT-authenticated access to user data
 * - Supports profile-based exercise personalization
 * 
 * Database Tables Used:
 * - user: Primary user profile information
 * - injury: User injury history and restrictions
 * 
 * @author PhysioApp Team
 * @version 1.0
 * @since 2025-01-01
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserProfileController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    
    public UserProfileController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * Retrieves the complete user profile for the authenticated user.
     * 
     * This endpoint fetches comprehensive user information including personal
     * details, fitness goals, injury history, and equipment access. The data
     * is used to personalize exercise recommendations and track user progress.
     * 
     * @param authHeader JWT authentication token
     * @return ResponseEntity containing user profile data or error details
     * 
     * @apiNote Returns user profile with the following structure:
     *          {
     *            "success": true,
     *            "user": {
     *              "userId": "uuid",
     *              "username": "string",
     *              "name": "string",
     *              "email": "string",
     *              "age": number,
     *              "gender": "string",
     *              "weight": number,
     *              "height": number,
     *              "fitnessGoal": "string",
     *              "injuryHistory": "string",
     *              "chronicDiseases": "string",
     *              "equipmentAccess": "string",
     *              "phone": "string",
     *              "role": "MEMBER"
     *            }
     *          }
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String token = authHeader.replace("Bearer ", "");
            if (!jwtUtil.validateToken(token)) {
                response.put("success", false);
                response.put("message", "Invalid token");
                return ResponseEntity.status(401).body(response);
            }
            
            String username = jwtUtil.extractUsername(token);
            User user = userRepository.findByUsername(username);
            
            if (user == null) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(404).body(response);
            }
            
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("userId", user.getUserId().toString());
            userProfile.put("username", user.getUsername());
            userProfile.put("email", user.getEmail() != null ? user.getEmail() : "");
            userProfile.put("name", user.getName() != null ? user.getName() : "");
            userProfile.put("gender", user.getGender() != null ? user.getGender() : "");
            userProfile.put("age", user.getAge() != null ? user.getAge() : 0);
            userProfile.put("phone", user.getPhone() != null ? user.getPhone() : "");
            userProfile.put("height", user.getHeight() != null ? user.getHeight() : 0.0);
            userProfile.put("weight", user.getWeight() != null ? user.getWeight() : 0.0);
            userProfile.put("chronicDiseases", user.getChronicDiseases() != null ? user.getChronicDiseases() : "");
            userProfile.put("injuryHistory", user.getInjuryHistory() != null ? user.getInjuryHistory() : "");
            userProfile.put("fitnessGoal", user.getFitnessGoal() != null ? user.getFitnessGoal() : "");
            userProfile.put("equipmentAccess", user.getEquipmentAccess() != null ? user.getEquipmentAccess() : "");
            userProfile.put("role", user.getRole());
            
            response.put("success", true);
            response.put("user", userProfile);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting user profile", e);
            response.put("success", false);
            response.put("message", "Error retrieving profile: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Updates specific user profile fields.
     * 
     * This endpoint allows users to update their profile information including
     * personal details, fitness goals, and health information. It handles
     * null/empty values safely and validates input data before updating.
     * 
     * @param authHeader JWT authentication token
     * @param updates Map containing field names and new values to update
     * @return ResponseEntity with success status and confirmation message
     * 
     * @apiNote Expected updates structure:
     *          {
     *            "name": "New Name",
     *            "age": 25,
     *            "fitnessGoal": "Weight Loss",
     *            "equipmentAccess": "Dumbbells, Resistance Bands",
     *            "injuryHistory": "Previous knee injury",
     *            "chronicDiseases": "None",
     *            "phone": "+1234567890",
     *            "email": "user@example.com"
     *          }
     * 
     * Supported fields:
     * - name, age, gender, weight, height
     * - fitnessGoal, injuryHistory, chronicDiseases
     * - equipmentAccess, phone, email
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> profileData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String token = authHeader.replace("Bearer ", "");
            if (!jwtUtil.validateToken(token)) {
                response.put("success", false);
                response.put("message", "Invalid token");
                return ResponseEntity.status(401).body(response);
            }
            
            String username = jwtUtil.extractUsername(token);
            User user = userRepository.findByUsername(username);
            
            if (user == null) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(404).body(response);
            }
            
            // Update user fields - handle null values properly
            if (profileData.containsKey("name")) {
                user.setName((String) profileData.get("name"));
            }
            if (profileData.containsKey("email")) {
                String email = (String) profileData.get("email");
                user.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : null);
            }
            if (profileData.containsKey("gender")) {
                user.setGender((String) profileData.get("gender"));
            }
            if (profileData.containsKey("age")) {
                Object ageObj = profileData.get("age");
                if (ageObj instanceof Number) {
                    user.setAge(((Number) ageObj).intValue());
                }
            }
            if (profileData.containsKey("phone")) {
                user.setPhone((String) profileData.get("phone"));
            }
            if (profileData.containsKey("height")) {
                Object heightObj = profileData.get("height");
                if (heightObj instanceof Number) {
                    user.setHeight(((Number) heightObj).doubleValue());
                }
            }
            if (profileData.containsKey("weight")) {
                Object weightObj = profileData.get("weight");
                if (weightObj instanceof Number) {
                    user.setWeight(((Number) weightObj).doubleValue());
                }
            }
            if (profileData.containsKey("chronicDiseases")) {
                user.setChronicDiseases((String) profileData.get("chronicDiseases"));
            }
            if (profileData.containsKey("injuryHistory")) {
                user.setInjuryHistory((String) profileData.get("injuryHistory"));
            }
            if (profileData.containsKey("fitnessGoal")) {
                user.setFitnessGoal((String) profileData.get("fitnessGoal"));
            }
            if (profileData.containsKey("equipmentAccess")) {
                user.setEquipmentAccess((String) profileData.get("equipmentAccess"));
            }
            
            userRepository.save(user);
            
            response.put("success", true);
            response.put("message", "Profile updated successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error updating user profile", e);
            response.put("success", false);
            response.put("message", "Error updating profile: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/session")
    public Map<String, Object> saveSessionData(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> sessionData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String token = authHeader.replace("Bearer ", "");
            if (!jwtUtil.validateToken(token)) {
                response.put("success", false);
                response.put("message", "Invalid token");
                return response;
            }

            String username = jwtUtil.extractUsername(token);
            User user = userRepository.findByUsername(username);
            
            if (user == null) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }

            // Save session-specific data (like stepper data, temporary preferences)
            // This could be stored in a separate session table or as JSON in user table
            // For now, we'll save relevant data to user profile
            
            if (sessionData.containsKey("stepperData")) {
                Map<String, Object> stepperData = (Map<String, Object>) sessionData.get("stepperData");
                
                // Extract and save relevant stepper data to user profile
                if (stepperData.containsKey("fitnessGoal")) {
                    user.setFitnessGoal((String) stepperData.get("fitnessGoal"));
                }
                if (stepperData.containsKey("equipmentAccess")) {
                    user.setEquipmentAccess((String) stepperData.get("equipmentAccess"));
                }
                if (stepperData.containsKey("injuryHistory")) {
                    user.setInjuryHistory((String) stepperData.get("injuryHistory"));
                }
                
                userRepository.save(user);
            }
            
            response.put("success", true);
            response.put("message", "Session data saved successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error saving session data: " + e.getMessage());
        }
        
        return response;
    }
} 