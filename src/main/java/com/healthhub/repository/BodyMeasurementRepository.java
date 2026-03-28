package com.healthhub.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.healthhub.entity.BodyMeasurement;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BodyMeasurementRepository extends JpaRepository<BodyMeasurement, UUID> {

    Page<BodyMeasurement> findByUserProfileId(UUID userProfileId, Pageable pageable);

    List<BodyMeasurement> findByUserProfileIdAndDateTakenBetween(UUID userProfileId, LocalDate from, LocalDate to);
}
