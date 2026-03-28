package com.healthhub.dto;

import java.time.Instant;
import java.util.UUID;

public record SleepLogDTO(
        UUID id,
        Instant creationDate,
        Instant bedTime,
        Instant wakeTime
) {
}
