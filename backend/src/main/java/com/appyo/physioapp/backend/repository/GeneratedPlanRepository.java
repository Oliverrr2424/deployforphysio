package com.appyo.physioapp.backend.repository;

import com.appyo.physioapp.backend.model.GeneratedPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeneratedPlanRepository extends JpaRepository<GeneratedPlan, UUID> {
    
    @Query("SELECT gp FROM GeneratedPlan gp WHERE gp.userId = :userId AND gp.isActive = true AND gp.expiresAt > :now ORDER BY gp.createdAt DESC")
    Optional<GeneratedPlan> findLatestActiveByUserId(@Param("userId") UUID userId, @Param("now") LocalDateTime now);
    
    @Query("SELECT gp FROM GeneratedPlan gp WHERE gp.userId = :userId AND gp.isActive = true AND gp.expiresAt > :now")
    List<GeneratedPlan> findAllActiveByUserId(@Param("userId") UUID userId, @Param("now") LocalDateTime now);
    
    @Query("SELECT gp FROM GeneratedPlan gp WHERE gp.isActive = true AND gp.expiresAt <= :now")
    List<GeneratedPlan> findExpiredPlans(@Param("now") LocalDateTime now);
    
    @Modifying
    @Transactional
    @Query("UPDATE GeneratedPlan gp SET gp.isActive = false WHERE gp.expiresAt <= :now")
    void deactivateExpiredPlans(@Param("now") LocalDateTime now);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM GeneratedPlan gp WHERE gp.expiresAt <= :cutoffDate")
    void deleteExpiredPlans(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Modifying
    @Transactional
    @Query("UPDATE GeneratedPlan gp SET gp.isUsed = true WHERE gp.planId = :planId")
    void markAsUsed(@Param("planId") UUID planId);
    
    @Query("SELECT COUNT(gp) FROM GeneratedPlan gp WHERE gp.userId = :userId AND gp.isActive = true")
    long countActiveByUserId(@Param("userId") UUID userId);
} 