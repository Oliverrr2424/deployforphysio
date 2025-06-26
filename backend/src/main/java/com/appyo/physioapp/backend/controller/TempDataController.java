package com.appyo.physioapp.backend.controller;

import com.appyo.physioapp.backend.model.GeneratedPlan;
import com.appyo.physioapp.backend.model.UserPreferences;
import com.appyo.physioapp.backend.service.TempDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/temp")
@CrossOrigin(origins = "http://localhost:3000")
public class TempDataController {
    
    private static final Logger logger = LoggerFactory.getLogger(TempDataController.class);
    
    @Autowired
    private TempDataService tempDataService;
    
    // User Preferences Endpoints
    @PostMapping("/preferences/{userId}")
    public ResponseEntity<?> saveUserPreferences(
            @PathVariable String userId,
            @RequestBody Map<String, Object> preferences) {
        try {
            UUID userUuid = UUID.fromString(userId);
            
            String fitnessLevel = (String) preferences.get("fitnessLevel");
            Integer workoutDuration = (Integer) preferences.get("workoutDuration");
            String equipmentAccess = (String) preferences.get("equipmentAccess");
            String injuryConsiderations = (String) preferences.get("injuryConsiderations");
            String fitnessGoals = (String) preferences.get("fitnessGoals");
            
            UserPreferences savedPreferences = tempDataService.saveUserPreferences(
                userUuid, fitnessLevel, workoutDuration, equipmentAccess, injuryConsiderations, fitnessGoals);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("preferenceId", savedPreferences.getPreferenceId());
            response.put("message", "User preferences saved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error saving user preferences", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to save preferences"));
        }
    }
    
    @GetMapping("/preferences/{userId}")
    public ResponseEntity<?> getUserPreferences(@PathVariable String userId) {
        try {
            UUID userUuid = UUID.fromString(userId);
            Optional<UserPreferences> preferences = tempDataService.getUserPreferences(userUuid);
            
            if (preferences.isPresent()) {
                UserPreferences pref = preferences.get();
                Map<String, Object> response = new HashMap<>();
                response.put("fitnessLevel", pref.getFitnessLevel());
                response.put("workoutDuration", pref.getWorkoutDuration());
                response.put("equipmentAccess", pref.getEquipmentAccess());
                response.put("injuryConsiderations", pref.getInjuryConsiderations());
                response.put("fitnessGoals", pref.getFitnessGoals());
                response.put("createdAt", pref.getCreatedAt());
                response.put("expiresAt", pref.getExpiresAt());
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error retrieving user preferences", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to retrieve preferences"));
        }
    }
    
    @GetMapping("/preferences/{userId}/exists")
    public ResponseEntity<?> checkPreferencesExist(@PathVariable String userId) {
        try {
            UUID userUuid = UUID.fromString(userId);
            boolean exists = tempDataService.hasActivePreferences(userUuid);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (Exception e) {
            logger.error("Error checking preferences existence", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to check preferences"));
        }
    }
    
    // Generated Plan Endpoints
    @PostMapping("/plans/{userId}")
    public ResponseEntity<?> saveGeneratedPlan(
            @PathVariable String userId,
            @RequestBody String planData) {
        try {
            UUID userUuid = UUID.fromString(userId);
            GeneratedPlan savedPlan = tempDataService.saveGeneratedPlan(userUuid, planData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("planId", savedPlan.getPlanId());
            response.put("message", "Generated plan saved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error saving generated plan", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to save plan"));
        }
    }
    
    @GetMapping("/plans/{userId}")
    public ResponseEntity<?> getLatestGeneratedPlan(@PathVariable String userId) {
        try {
            UUID userUuid = UUID.fromString(userId);
            Optional<GeneratedPlan> plan = tempDataService.getLatestGeneratedPlan(userUuid);
            
            if (plan.isPresent()) {
                GeneratedPlan generatedPlan = plan.get();
                Map<String, Object> response = new HashMap<>();
                response.put("planId", generatedPlan.getPlanId());
                response.put("planData", generatedPlan.getPlanData());
                response.put("createdAt", generatedPlan.getCreatedAt());
                response.put("expiresAt", generatedPlan.getExpiresAt());
                response.put("isUsed", generatedPlan.getIsUsed());
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error retrieving generated plan", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to retrieve plan"));
        }
    }
    
    @GetMapping("/plans/{userId}/exists")
    public ResponseEntity<?> checkPlanExists(@PathVariable String userId) {
        try {
            UUID userUuid = UUID.fromString(userId);
            boolean exists = tempDataService.hasActivePlan(userUuid);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (Exception e) {
            logger.error("Error checking plan existence", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to check plan"));
        }
    }
    
    @PutMapping("/plans/{planId}/mark-used")
    public ResponseEntity<?> markPlanAsUsed(@PathVariable String planId) {
        try {
            UUID planUuid = UUID.fromString(planId);
            tempDataService.markPlanAsUsed(planUuid);
            return ResponseEntity.ok(Map.of("success", true, "message", "Plan marked as used"));
        } catch (Exception e) {
            logger.error("Error marking plan as used", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to mark plan as used"));
        }
    }
    
    // Cleanup endpoint for testing
    @PostMapping("/cleanup")
    public ResponseEntity<?> forceCleanup() {
        try {
            tempDataService.forceCleanup();
            return ResponseEntity.ok(Map.of("success", true, "message", "Cleanup completed"));
        } catch (Exception e) {
            logger.error("Error during cleanup", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Cleanup failed"));
        }
    }
} 