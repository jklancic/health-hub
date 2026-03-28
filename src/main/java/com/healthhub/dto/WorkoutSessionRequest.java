package com.healthhub.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record WorkoutSessionRequest(
        UUID workoutPlanId,
        Instant startedAt,
        Instant finishedAt,
        boolean deload,
        List<WorkoutTrackDTO> tracks
) {}
