package com.healthhub.service.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.healthhub.dto.BodyMeasurementDTO;
import com.healthhub.entity.BodyMeasurement;
import com.healthhub.entity.UserProfile;
import com.healthhub.exception.ResourceNotFoundException;
import com.healthhub.repository.BodyMeasurementRepository;
import com.healthhub.repository.UserProfileRepository;
import com.healthhub.service.BodyMeasurementService;

@Service
public class BodyMeasurementServiceImpl implements BodyMeasurementService {

    private static final Logger log = LoggerFactory.getLogger(BodyMeasurementServiceImpl.class);

    private final BodyMeasurementRepository bodyMeasurementRepository;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public BodyMeasurementServiceImpl(BodyMeasurementRepository bodyMeasurementRepository,
                                      UserProfileRepository userProfileRepository) {
        this.bodyMeasurementRepository = bodyMeasurementRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public BodyMeasurement createBodyMeasurement(UUID userId, BodyMeasurementDTO dto) {
        log.debug("Creating body measurement for user ID: {}", userId);
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("User profile not found for user ID: {}", userId);
                    return new ResourceNotFoundException("User profile not found for user with id " + userId);
                });

        BodyMeasurement measurement = new BodyMeasurement();
        measurement.setUserProfile(userProfile);
        measurement.setDateTaken(dto.dateTaken());
        measurement.setWeight(dto.weight());
        measurement.setWaist(dto.waist());

        BodyMeasurement saved = bodyMeasurementRepository.save(measurement);
        log.info("Created body measurement with ID: {}", saved.getId());
        return saved;
    }

    @Override
    public BodyMeasurement getBodyMeasurement(UUID id) {
        log.debug("Fetching body measurement with ID: {}", id);
        return bodyMeasurementRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Body measurement not found with ID: {}", id);
                    return new ResourceNotFoundException("Body measurement not found with id " + id);
                });
    }

    @Override
    public Page<BodyMeasurement> getBodyMeasurements(UUID userId, Pageable pageable) {
        log.debug("Fetching body measurements for user ID: {}", userId);
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("User profile not found for user ID: {}", userId);
                    return new ResourceNotFoundException("User profile not found for user with id " + userId);
                });
        return bodyMeasurementRepository.findByUserProfileId(userProfile.getId(), pageable);
    }

    @Override
    public BodyMeasurement updateBodyMeasurement(UUID id, BodyMeasurementDTO dto) {
        log.debug("Updating body measurement with ID: {}", id);
        BodyMeasurement existing = bodyMeasurementRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Body measurement not found with ID: {}", id);
                    return new ResourceNotFoundException("Body measurement not found with id " + id);
                });

        existing.setDateTaken(dto.dateTaken());
        existing.setWeight(dto.weight());
        existing.setWaist(dto.waist());

        BodyMeasurement updated = bodyMeasurementRepository.save(existing);
        log.info("Updated body measurement with ID: {}", updated.getId());
        return updated;
    }

    @Override
    public void deleteBodyMeasurement(UUID id) {
        log.debug("Deleting body measurement with ID: {}", id);
        if (!bodyMeasurementRepository.existsById(id)) {
            log.warn("Body measurement not found with ID: {}", id);
            throw new ResourceNotFoundException("Body measurement not found with id " + id);
        }
        bodyMeasurementRepository.deleteById(id);
        log.info("Deleted body measurement with ID: {}", id);
    }
}
