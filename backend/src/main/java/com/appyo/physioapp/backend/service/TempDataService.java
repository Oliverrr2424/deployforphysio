package com.appyo.physioapp.backend.service;

import com.appyo.physioapp.backend.model.GeneratedPlan;
import com.appyo.physioapp.backend.model.UserPreferences;
import com.appyo.physioapp.backend.repository.GeneratedPlanRepository;
import com.appyo.physioapp.backend.repository.UserPreferencesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TempDataService {
    
    private static final Logger logger = LoggerFactory.getLogger(TempDataService.class);
    
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;
    
    @Autowired
    private GeneratedPlanRepository generatedPlanRepository;
    
    // User Preferences Methods
    public UserPreferences saveUserPreferences(UUID userId, String fitnessLevel, Integer workoutDuration,
                                              String equipmentAccess, String injuryConsiderations, String fitnessGoals) {
        // Deactivate any existing active preferences for this user
        Optional<UserPreferences> existing = userPreferencesRepository.findActiveByUserId(userId, LocalDateTime.now());
        if (existing.isPresent()) {
            UserPreferences existingPref = existing.get();
            existingPref.setIsActive(false);
            userPreferencesRepository.save(existingPref);
        }
        
        // Create new preferences
        UserPreferences preferences = new UserPreferences(userId, fitnessLevel, workoutDuration, 
                                                        equipmentAccess, injuryConsiderations, fitnessGoals);
        return userPreferencesRepository.save(preferences);
    }
    
    public Optional<UserPreferences> getUserPreferences(UUID userId) {
        return userPreferencesRepository.findActiveByUserId(userId, LocalDateTime.now());
    }
    
    public boolean hasActivePreferences(UUID userId) {
        return userPreferencesRepository.countActiveByUserId(userId) > 0;
    }
    
    // Generated Plan Methods
    public GeneratedPlan saveGeneratedPlan(UUID userId, String planData) {
        // Deactivate any existing active plans for this user
        Optional<GeneratedPlan> existing = generatedPlanRepository.findLatestActiveByUserId(userId, LocalDateTime.now());
        if (existing.isPresent()) {
            GeneratedPlan existingPlan = existing.get();
            existingPlan.setIsActive(false);
            generatedPlanRepository.save(existingPlan);
        }
        
        // Create new plan
        GeneratedPlan plan = new GeneratedPlan(userId, planData);
        return generatedPlanRepository.save(plan);
    }
    
    public Optional<GeneratedPlan> getLatestGeneratedPlan(UUID userId) {
        return generatedPlanRepository.findLatestActiveByUserId(userId, LocalDateTime.now());
    }
    
    public List<GeneratedPlan> getAllActivePlans(UUID userId) {
        return generatedPlanRepository.findAllActiveByUserId(userId, LocalDateTime.now());
    }
    
    public void markPlanAsUsed(UUID planId) {
        generatedPlanRepository.markAsUsed(planId);
    }
    
    public boolean hasActivePlan(UUID userId) {
        return generatedPlanRepository.countActiveByUserId(userId) > 0;
    }
    
    // Cleanup Methods
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredData() {
        logger.info("Starting cleanup of expired temporary data...");
        LocalDateTime now = LocalDateTime.now();
        
        // Cleanup expired preferences
        List<UserPreferences> expiredPreferences = userPreferencesRepository.findExpiredPreferences(now);
        if (!expiredPreferences.isEmpty()) {
            userPreferencesRepository.deactivateExpiredPreferences(now);
            logger.info("Deactivated {} expired user preferences", expiredPreferences.size());
        }
        
        // Cleanup expired plans
        List<GeneratedPlan> expiredPlans = generatedPlanRepository.findExpiredPlans(now);
        if (!expiredPlans.isEmpty()) {
            generatedPlanRepository.deactivateExpiredPlans(now);
            logger.info("Deactivated {} expired generated plans", expiredPlans.size());
        }
        
        // Delete very old data (older than 30 days)
        LocalDateTime cutoffDate = now.minusDays(30);
        userPreferencesRepository.deleteExpiredPreferences(cutoffDate);
        generatedPlanRepository.deleteExpiredPlans(cutoffDate);
        
        logger.info("Cleanup completed");
    }
    
    // Manual cleanup for testing
    public void forceCleanup() {
        logger.info("Force cleanup initiated");
        cleanupExpiredData();
    }
} 