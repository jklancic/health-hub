package com.healthhub.dto;

import java.time.LocalDate;

public record SleepWeeklyAverageDTO(
        LocalDate weekStart,
        LocalDate weekEnd,
        Long avgHours,
        Long avgMinutes,
        Long avgseconds,
        int entries
) {
}
