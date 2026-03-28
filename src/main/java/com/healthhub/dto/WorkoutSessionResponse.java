package com.healthhub.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record WorkoutSessionResponse(
        UUID id,
        UUID workoutPlanId,
        String workoutPlanName,
        Instant startedAt,
        Instant finishedAt,
        boolean deload,
        List<WorkoutTrackDTO> tracks
) {}
