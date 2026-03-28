package com.healthhub.service;

import com.healthhub.dto.SleepLogDTO;
import com.healthhub.dto.SleepWeeklyAverageDTO;
import com.healthhub.entity.SleepLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface SleepLogService {

    SleepLog createSleepLog(UUID userId, SleepLogDTO dto);

    SleepLog getSleepLog(UUID id);

    Page<SleepLog> getSleepLogs(UUID userId, Pageable pageable);

    void deleteSleepLog(UUID id);

    List<SleepWeeklyAverageDTO> getWeeklyAverages(UUID userId, int weeks);
}
