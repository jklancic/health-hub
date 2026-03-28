package com.healthhub.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BodyMeasurementDTO(
    UUID id,
    Instant creationDate,
    LocalDate dateTaken,
    Double weight,
    Double waist
) {}
