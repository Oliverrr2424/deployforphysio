package com.appyo.physioapp.backend.repository;

import com.appyo.physioapp.backend.model.UserPreferences;
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
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, UUID> {
    
    @Query("SELECT up FROM UserPreferences up WHERE up.userId = :userId AND up.isActive = true AND up.expiresAt > :now")
    Optional<UserPreferences> findActiveByUserId(@Param("userId") UUID userId, @Param("now") LocalDateTime now);
    
    @Query("SELECT up FROM UserPreferences up WHERE up.isActive = true AND up.expiresAt <= :now")
    List<UserPreferences> findExpiredPreferences(@Param("now") LocalDateTime now);
    
    @Modifying
    @Transactional
    @Query("UPDATE UserPreferences up SET up.isActive = false WHERE up.expiresAt <= :now")
    void deactivateExpiredPreferences(@Param("now") LocalDateTime now);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM UserPreferences up WHERE up.expiresAt <= :cutoffDate")
    void deleteExpiredPreferences(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("SELECT COUNT(up) FROM UserPreferences up WHERE up.userId = :userId AND up.isActive = true")
    long countActiveByUserId(@Param("userId") UUID userId);
} 