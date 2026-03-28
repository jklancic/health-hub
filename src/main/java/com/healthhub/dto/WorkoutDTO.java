package com.healthhub.dto;

import java.util.UUID;

public record WorkoutDTO(
        UUID id,
        String name,
        int sets,
        int repsMin,
        int repsMax,
        int restSeconds
) {}
