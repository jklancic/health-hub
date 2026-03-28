package com.healthhub.dto;

import java.util.List;
import java.util.UUID;

public record WorkoutPlanResponse(
        UUID id,
        String name,
        List<WorkoutDTO> workouts
) {}
