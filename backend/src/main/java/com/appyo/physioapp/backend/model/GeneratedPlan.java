package com.appyo.physioapp.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "generated_plans")
public class GeneratedPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "plan_id")
    private UUID planId;
    
    @Column(name = "user_id")
    private UUID userId;
    
    @Column(name = "plan_data", columnDefinition = "TEXT")
    private String planData; // JSON string containing the generated plan
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "is_used")
    private Boolean isUsed = false;

    // Constructors
    public GeneratedPlan() {}
    
    public GeneratedPlan(UUID userId, String planData) {
        this.userId = userId;
        this.planData = planData;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusDays(1); // Expires in 1 day
        this.isActive = true;
        this.isUsed = false;
    }

    // Getters and Setters
    public UUID getPlanId() {
        return planId;
    }

    public void setPlanId(UUID planId) {
        this.planId = planId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getPlanData() {
        return planData;
    }

    public void setPlanData(String planData) {
        this.planData = planData;
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

    public Boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
} 