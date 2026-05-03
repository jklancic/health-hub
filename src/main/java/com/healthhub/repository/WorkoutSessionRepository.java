package com.healthhub.repository;

import com.healthhub.entity.WorkoutSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, UUID> {

    @Query("SELECT s FROM WorkoutSession s WHERE s.user.id = :userId ORDER BY s.startedAt DESC")
    Page<WorkoutSession> findByUserIdOrderByDateDesc(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT s FROM WorkoutSession s WHERE s.id = :id AND s.user.id = :userId")
    Optional<WorkoutSession> findByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END FROM WorkoutSession s WHERE s.workoutPlan.id = :workoutPlanId")
    boolean existsByWorkoutPlanId(@Param("workoutPlanId") UUID workoutPlanId);
}
