package com.healthhub.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthhub.dto.BodyMeasurementDTO;
import com.healthhub.entity.BodyMeasurement;
import com.healthhub.service.BodyMeasurementService;

@RestController
@RequestMapping("/api/users/{userId}/profile/measurements")
public class BodyMeasurementController {

    private final BodyMeasurementService bodyMeasurementService;

    @Autowired
    public BodyMeasurementController(BodyMeasurementService bodyMeasurementService) {
        this.bodyMeasurementService = bodyMeasurementService;
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @PostMapping
    public ResponseEntity<BodyMeasurement> createBodyMeasurement(
            @PathVariable UUID userId,
            @RequestBody BodyMeasurementDTO dto) {
        BodyMeasurement created = bodyMeasurementService.createBodyMeasurement(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @GetMapping
    public ResponseEntity<Page<BodyMeasurement>> getBodyMeasurements(
            @PathVariable UUID userId,
            @PageableDefault(size = 50, sort = "dateTaken", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BodyMeasurement> measurements = bodyMeasurementService.getBodyMeasurements(userId, pageable);
        return ResponseEntity.ok(measurements);
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @GetMapping("/{measurementId}")
    public ResponseEntity<BodyMeasurement> getBodyMeasurement(
            @PathVariable UUID userId,
            @PathVariable UUID measurementId) {
        BodyMeasurement measurement = bodyMeasurementService.getBodyMeasurement(measurementId);
        return ResponseEntity.ok(measurement);
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @PutMapping("/{measurementId}")
    public ResponseEntity<BodyMeasurement> updateBodyMeasurement(
            @PathVariable UUID userId,
            @PathVariable UUID measurementId,
            @RequestBody BodyMeasurementDTO dto) {
        BodyMeasurement updated = bodyMeasurementService.updateBodyMeasurement(measurementId, dto);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @DeleteMapping("/{measurementId}")
    public ResponseEntity<Void> deleteBodyMeasurement(
            @PathVariable UUID userId,
            @PathVariable UUID measurementId) {
        bodyMeasurementService.deleteBodyMeasurement(measurementId);
        return ResponseEntity.noContent().build();
    }
}
