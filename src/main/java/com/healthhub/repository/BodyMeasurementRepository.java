package com.healthhub.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.healthhub.entity.UserProfile;

@Repository
public interface BodyMeasurementRepository extends JpaRepository<UserProfile, UUID> {
    
}
