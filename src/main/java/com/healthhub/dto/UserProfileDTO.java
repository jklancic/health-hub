package com.healthhub.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.healthhub.entity.Gender;
import com.healthhub.entity.SystemOfUnits;

public record UserProfileDTO(

    UUID id,
    LocalDate birthDate,
    Double height,
    Gender gender,
    SystemOfUnits units
) {}