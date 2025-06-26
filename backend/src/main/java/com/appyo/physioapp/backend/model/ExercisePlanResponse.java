package com.appyo.physioapp.backend.model;

import java.util.List;

public class ExercisePlanResponse {
    private String title;
    private String description;
    private String disclaimer;
    private List<Exercise> exercises;
    private String planName;
    private String duration;
    private String targetedAreas;

    public ExercisePlanResponse() {
    }

    public ExercisePlanResponse(String title, String description, String disclaimer, List<Exercise> exercises) {
        this.title = title;
        this.description = description;
        this.disclaimer = disclaimer;
        this.exercises = exercises;
    }

    public static class Exercise {
        private String name;
        private String description;
        private String sets;
        private String reps;
        private String equipment;
        private String difficulty;
        private String instructions;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getSets() { return sets; }
        public void setSets(String sets) { this.sets = sets; }
        public String getReps() { return reps; }
        public void setReps(String reps) { this.reps = reps; }
        public String getEquipment() { return equipment; }
        public void setEquipment(String equipment) { this.equipment = equipment; }
        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
        public String getInstructions() { return instructions; }
        public void setInstructions(String instructions) { this.instructions = instructions; }
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }
    
    public String getPlanName() {
        return planName;
    }
    
    public void setPlanName(String planName) {
        this.planName = planName;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public String getTargetedAreas() {
        return targetedAreas;
    }
    
    public void setTargetedAreas(String targetedAreas) {
        this.targetedAreas = targetedAreas;
    }
} 