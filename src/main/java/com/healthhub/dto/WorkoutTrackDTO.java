package com.healthhub.dto;

import java.util.UUID;

public record WorkoutTrackDTO(
        UUID id,
        UUID workoutId,
        String workoutName,
        int sets,
        int reps,
        double weight
) {}
