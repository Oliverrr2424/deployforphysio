package com.appyo.physioapp.backend.presentation.dto;

import com.appyo.physioapp.user.User;
import java.time.LocalDateTime;

/**
 * DTO for user responses (excludes sensitive information like password)
 */
public class UserResponse {
    
    private String userId;
    private String username;
    private String email;
    private String name;
    private String gender;
    private Integer age;
    private String phone;
    private Double height;
    private Double weight;
    private String chronicDiseases;
    private String injuryHistory;
    private String fitnessGoal;
    private String equipmentAccess;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    
    // Default constructor
    public UserResponse() {}
    
    // Constructor from User entity
    public UserResponse(User user) {
        this.userId = user.getUserId().toString();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.name = user.getName();
        this.gender = user.getGender();
        this.age = user.getAge();
        this.phone = user.getPhone();
        this.height = user.getHeight();
        this.weight = user.getWeight();
        this.chronicDiseases = user.getChronicDiseases();
        this.injuryHistory = user.getInjuryHistory();
        this.fitnessGoal = user.getFitnessGoal();
        this.equipmentAccess = user.getEquipmentAccess();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
        this.lastLogin = user.getLastLogin();
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Double getHeight() {
        return height;
    }
    
    public void setHeight(Double height) {
        this.height = height;
    }
    
    public Double getWeight() {
        return weight;
    }
    
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    
    public String getChronicDiseases() {
        return chronicDiseases;
    }
    
    public void setChronicDiseases(String chronicDiseases) {
        this.chronicDiseases = chronicDiseases;
    }
    
    public String getInjuryHistory() {
        return injuryHistory;
    }
    
    public void setInjuryHistory(String injuryHistory) {
        this.injuryHistory = injuryHistory;
    }
    
    public String getFitnessGoal() {
        return fitnessGoal;
    }
    
    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }
    
    public String getEquipmentAccess() {
        return equipmentAccess;
    }
    
    public void setEquipmentAccess(String equipmentAccess) {
        this.equipmentAccess = equipmentAccess;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
} 