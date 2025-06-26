package com.appyo.physioapp.backend.model;

import java.util.List;

/**
 * ExercisePlanRequest
 * 
 * Data transfer object for exercise plan generation requests.
 * Contains user preferences and session data needed to generate
 * personalized exercise plans.
 * 
 * @author PhysioApp Team
 * @version 1.0
 * @since 2025-01-01
 */
public class ExercisePlanRequest {
    private String userId;
    private String duration;
    private List<String> targetedAreas;
    private String difficulty;
    private String equipment;
    private String goals;
    private String notes;
    private List<String> excludedAreas;
    private String focusType;
    
    // Default constructor
    public ExercisePlanRequest() {}
    
    // Constructor with all fields
    public ExercisePlanRequest(String userId, String duration, List<String> targetedAreas, 
                              String difficulty, String equipment, String goals, String notes,
                              List<String> excludedAreas, String focusType) {
        this.userId = userId;
        this.duration = duration;
        this.targetedAreas = targetedAreas;
        this.difficulty = difficulty;
        this.equipment = equipment;
        this.goals = goals;
        this.notes = notes;
        this.excludedAreas = excludedAreas;
        this.focusType = focusType;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public List<String> getTargetedAreas() {
        return targetedAreas;
    }
    
    public void setTargetedAreas(List<String> targetedAreas) {
        this.targetedAreas = targetedAreas;
    }
    
    public String getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    
    public String getEquipment() {
        return equipment;
    }
    
    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }
    
    public String getGoals() {
        return goals;
    }
    
    public void setGoals(String goals) {
        this.goals = goals;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public List<String> getExcludedAreas() {
        return excludedAreas;
    }
    
    public void setExcludedAreas(List<String> excludedAreas) {
        this.excludedAreas = excludedAreas;
    }
    
    public String getFocusType() {
        return focusType;
    }
    
    public void setFocusType(String focusType) {
        this.focusType = focusType;
    }
    
    @Override
    public String toString() {
        return "ExercisePlanRequest{" +
                "userId='" + userId + '\'' +
                ", duration='" + duration + '\'' +
                ", targetedAreas=" + targetedAreas +
                ", difficulty='" + difficulty + '\'' +
                ", equipment='" + equipment + '\'' +
                ", goals='" + goals + '\'' +
                ", notes='" + notes + '\'' +
                ", excludedAreas=" + excludedAreas +
                ", focusType='" + focusType + '\'' +
                '}';
    }
} 