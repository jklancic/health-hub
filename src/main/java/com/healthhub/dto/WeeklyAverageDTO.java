package com.healthhub.dto;

import java.time.LocalDate;

public record WeeklyAverageDTO(
    LocalDate weekStart,
    LocalDate weekEnd,
    Double avgWeight,
    Double avgWaist,
    int entries
) {}
