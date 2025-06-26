package com.appyo.physioapp.backend.model;

public class Exercise {
    private String name;
    private String description;
    private int sets;
    private int reps;
    private String duration;
    private String difficulty;
    private String equipment;
    private String instructions;

    // Default constructor
    public Exercise() {
    }

    public Exercise(String name, String description, int sets, int reps, String duration, 
                   String difficulty, String equipment, String instructions) {
        this.name = name;
        this.description = description;
        this.sets = sets;
        this.reps = reps;
        this.duration = duration;
        this.difficulty = difficulty;
        this.equipment = equipment;
        this.instructions = instructions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
} 