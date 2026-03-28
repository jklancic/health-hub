package com.healthhub.service.impl;

import com.healthhub.dto.SleepLogDTO;
import com.healthhub.dto.SleepWeeklyAverageDTO;
import com.healthhub.entity.SleepLog;
import com.healthhub.entity.UserProfile;
import com.healthhub.exception.ResourceNotFoundException;
import com.healthhub.repository.SleepLogRepository;
import com.healthhub.repository.UserProfileRepository;
import com.healthhub.service.SleepLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SleepLogServiceImpl implements SleepLogService {

    private static final Logger log = LoggerFactory.getLogger(SleepLogServiceImpl.class);

    private final SleepLogRepository sleepLogRepository;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public SleepLogServiceImpl(SleepLogRepository sleepLogRepository,
                               UserProfileRepository userProfileRepository) {
        this.sleepLogRepository = sleepLogRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public SleepLog createSleepLog(UUID userId, SleepLogDTO dto) {
        log.debug("Creating sleep log for user ID: {}", userId);
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("User profile not found for user ID: {}", userId);
                    return new ResourceNotFoundException("User profile not found for user with id " + userId);
                });

        SleepLog sleepLog = new SleepLog();
        sleepLog.setUserProfile(userProfile);
        sleepLog.setBedTime(dto.bedTime());
        sleepLog.setWakeTime(dto.wakeTime());

        SleepLog saved = sleepLogRepository.save(sleepLog);
        log.info("Created sleep log with ID: {}", saved.getId());
        return saved;
    }

    @Override
    public SleepLog getSleepLog(UUID id) {
        log.debug("Fetching sleep log with ID: {}", id);
        return sleepLogRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Sleep log not found with ID: {}", id);
                    return new ResourceNotFoundException("Sleep log not found with id " + id);
                });
    }

    @Override
    public Page<SleepLog> getSleepLogs(UUID userId, Pageable pageable) {
        log.debug("Fetching sleep logs for user ID: {}", userId);
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("User profile not found for user ID: {}", userId);
                    return new ResourceNotFoundException("User profile not found for user with id " + userId);
                });
        return sleepLogRepository.findByUserProfileId(userProfile.getId(), pageable);
    }

    @Override
    public void deleteSleepLog(UUID id) {
        log.debug("Deleting sleep log with ID: {}", id);
        if (!sleepLogRepository.existsById(id)) {
            log.warn("Sleep log not found with ID: {}", id);
            throw new ResourceNotFoundException("Sleep log not found with id " + id);
        }
        sleepLogRepository.deleteById(id);
        log.info("Deleted sleep log with ID: {}", id);
    }

    @Override
    public List<SleepWeeklyAverageDTO> getWeeklyAverages(UUID userId, int weeks) {
        log.debug("Fetching weekly sleep averages for user ID: {}, weeks: {}", userId, weeks);
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("User profile not found for user ID: {}", userId);
                    return new ResourceNotFoundException("User profile not found for user with id " + userId);
                });

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate currentWeekMonday = today.with(DayOfWeek.MONDAY);
        LocalDate rangeStart = currentWeekMonday.minusWeeks(weeks - 1);

        Instant rangeStartInstant = rangeStart.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant rangeEndInstant = Instant.now();

        List<SleepLog> logs = sleepLogRepository
                .findByUserProfileIdAndBedTimeBetween(userProfile.getId(), rangeStartInstant, rangeEndInstant);

        Map<LocalDate, List<SleepLog>> byWeek = logs.stream()
                .filter(s -> s.getBedTime() != null && s.getWakeTime() != null)
                .collect(Collectors.groupingBy(s ->
                        s.getBedTime().atZone(ZoneOffset.UTC).toLocalDate().with(DayOfWeek.MONDAY)));

        List<SleepWeeklyAverageDTO> result = new ArrayList<>();
        for (int i = 0; i < weeks; i++) {
            LocalDate weekStart = currentWeekMonday.minusWeeks(weeks - 1 - i);
            LocalDate weekEnd = weekStart.plusDays(6);

            List<SleepLog> weekLogs = byWeek.getOrDefault(weekStart, List.of());

            OptionalDouble avgSeconds = weekLogs.stream()
                    .mapToLong(s -> Duration.between(s.getBedTime(), s.getWakeTime()).getSeconds())
                    .average();

            Long avgHours = null;
            Long avgMinutes = null;
            Long avgSecs = null;

            if (avgSeconds.isPresent()) {
                long totalSeconds = (long) avgSeconds.getAsDouble();
                avgHours = totalSeconds / 3600;
                avgMinutes = (totalSeconds % 3600) / 60;
                avgSecs = totalSeconds % 60;
            }

            result.add(new SleepWeeklyAverageDTO(weekStart, weekEnd, avgHours, avgMinutes, avgSecs, weekLogs.size()));
        }

        return result;
    }
}
