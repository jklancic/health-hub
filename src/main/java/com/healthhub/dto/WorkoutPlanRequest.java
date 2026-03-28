package com.healthhub.dto;

import java.util.List;

public record WorkoutPlanRequest(
        String name,
        List<WorkoutDTO> workouts
) {}
