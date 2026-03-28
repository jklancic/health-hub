package com.healthhub.service;

import com.healthhub.dto.WorkoutPlanRequest;
import com.healthhub.dto.WorkoutPlanResponse;

import java.util.List;
import java.util.UUID;

public interface WorkoutPlanService {

    WorkoutPlanResponse createPlan(UUID userId, WorkoutPlanRequest request);

    WorkoutPlanResponse getPlan(UUID userId, UUID planId);

    List<WorkoutPlanResponse> getPlans(UUID userId);

    WorkoutPlanResponse updatePlan(UUID userId, UUID planId, WorkoutPlanRequest request);

    void deletePlan(UUID userId, UUID planId);
}
