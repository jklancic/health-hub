package com.healthhub.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.healthhub.dto.BodyMeasurementDTO;
import com.healthhub.entity.BodyMeasurement;

public interface BodyMeasurementService {

    BodyMeasurement createBodyMeasurement(UUID userId, BodyMeasurementDTO dto);

    BodyMeasurement getBodyMeasurement(UUID id);

    Page<BodyMeasurement> getBodyMeasurements(UUID userId, Pageable pageable);

    BodyMeasurement updateBodyMeasurement(UUID id, BodyMeasurementDTO dto);

    void deleteBodyMeasurement(UUID id);
}
