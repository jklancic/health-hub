package com.healthhub.repository;

import com.healthhub.entity.SleepLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SleepLogRepository extends JpaRepository<SleepLog, UUID> {

    Page<SleepLog> findByUserProfileId(UUID userProfileId, Pageable pageable);

    List<SleepLog> findByUserProfileIdAndBedTimeBetween(UUID userProfileId, Instant from, Instant to);
}
