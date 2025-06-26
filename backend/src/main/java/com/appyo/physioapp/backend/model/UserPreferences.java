package com.appyo.physioapp.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_preferences")
public class UserPreferences {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "preference_id")
    private UUID preferenceId;
    
    @Column(name = "user_id")
    private UUID userId;
    
    @Column(name = "fitness_level")
    private String fitnessLevel;
    
    @Column(name = "workout_duration")
    private Integer workoutDuration;
    
    @Column(name = "equipment_access", columnDefinition = "TEXT")
    private String equipmentAccess;
    
    @Column(name = "injury_considerations", columnDefinition = "TEXT")
    private String injuryConsiderations;
    
    @Column(name = "fitness_goals", columnDefinition = "TEXT")
    private String fitnessGoals;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;

    // Constructors
    public UserPreferences() {}
    
    public UserPreferences(UUID userId, String fitnessLevel, Integer workoutDuration, 
                          String equipmentAccess, String injuryConsiderations, String fitnessGoals) {
        this.userId = userId;
        this.fitnessLevel = fitnessLevel;
        this.workoutDuration = workoutDuration;
        this.equipmentAccess = equipmentAccess;
        this.injuryConsiderations = injuryConsiderations;
        this.fitnessGoals = fitnessGoals;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusDays(7); // Expires in 7 days
        this.isActive = true;
    }

    // Getters and Setters
    public UUID getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(UUID preferenceId) {
        this.preferenceId = preferenceId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getFitnessLevel() {
        return fitnessLevel;
    }

    public void setFitnessLevel(String fitnessLevel) {
        this.fitnessLevel = fitnessLevel;
    }

    public Integer getWorkoutDuration() {
        return workoutDuration;
    }

    public void setWorkoutDuration(Integer workoutDuration) {
        this.workoutDuration = workoutDuration;
    }

    public String getEquipmentAccess() {
        return equipmentAccess;
    }

    public void setEquipmentAccess(String equipmentAccess) {
        this.equipmentAccess = equipmentAccess;
    }

    public String getInjuryConsiderations() {
        return injuryConsiderations;
    }

    public void setInjuryConsiderations(String injuryConsiderations) {
        this.injuryConsiderations = injuryConsiderations;
    }

    public String getFitnessGoals() {
        return fitnessGoals;
    }

    public void setFitnessGoals(String fitnessGoals) {
        this.fitnessGoals = fitnessGoals;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
} 