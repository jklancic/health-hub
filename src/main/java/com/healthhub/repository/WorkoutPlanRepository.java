package com.healthhub.repository;

import com.healthhub.entity.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, UUID> {

    @Query("SELECT p FROM WorkoutPlan p WHERE p.user.id = :userId ORDER BY p.name ASC")
    List<WorkoutPlan> findTop10ByUserId(@Param("userId") UUID userId);

    @Query("SELECT p FROM WorkoutPlan p WHERE p.id = :id AND p.user.id = :userId")
    Optional<WorkoutPlan> findByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);
}
