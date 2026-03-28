package com.healthhub.controller;

import com.healthhub.dto.SleepLogDTO;
import com.healthhub.dto.SleepWeeklyAverageDTO;
import com.healthhub.entity.SleepLog;
import com.healthhub.service.SleepLogService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/sleep")
public class SleepLogController {

    private final SleepLogService sleepLogService;

    @Autowired
    public SleepLogController(SleepLogService sleepLogService) {
        this.sleepLogService = sleepLogService;
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @PostMapping
    public ResponseEntity<SleepLog> createSleepLog(
            @PathVariable UUID userId,
            @RequestBody SleepLogDTO dto) {
        SleepLog created = sleepLogService.createSleepLog(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @GetMapping
    public ResponseEntity<Page<SleepLog>> getSleepLogs(
            @PathVariable UUID userId,
            @PageableDefault(size = 50, sort = "bedTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SleepLog> logs = sleepLogService.getSleepLogs(userId, pageable);
        return ResponseEntity.ok(logs);
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @GetMapping("/{sleepLogId}")
    public ResponseEntity<SleepLog> getSleepLog(
            @PathVariable UUID userId,
            @PathVariable UUID sleepLogId) {
        SleepLog log = sleepLogService.getSleepLog(sleepLogId);
        return ResponseEntity.ok(log);
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @DeleteMapping("/{sleepLogId}")
    public ResponseEntity<Void> deleteSleepLog(
            @PathVariable UUID userId,
            @PathVariable UUID sleepLogId) {
        sleepLogService.deleteSleepLog(sleepLogId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @GetMapping("/weekly-averages")
    public ResponseEntity<List<SleepWeeklyAverageDTO>> getWeeklyAverages(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "5") int weeks) {
        List<SleepWeeklyAverageDTO> averages = sleepLogService.getWeeklyAverages(userId, weeks);
        return ResponseEntity.ok(averages);
    }
}
