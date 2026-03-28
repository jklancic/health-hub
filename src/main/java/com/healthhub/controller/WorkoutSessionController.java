package com.healthhub.controller;

import com.healthhub.dto.WorkoutSessionRequest;
import com.healthhub.dto.WorkoutSessionResponse;
import com.healthhub.service.WorkoutSessionService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/workout-sessions")
public class WorkoutSessionController {

    private final WorkoutSessionService workoutSessionService;

    @Autowired
    public WorkoutSessionController(WorkoutSessionService workoutSessionService) {
        this.workoutSessionService = workoutSessionService;
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @PostMapping
    public ResponseEntity<WorkoutSessionResponse> createSession(
            @PathVariable UUID userId,
            @RequestBody WorkoutSessionRequest request) {
        WorkoutSessionResponse created = workoutSessionService.createSession(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @GetMapping
    public ResponseEntity<Page<WorkoutSessionResponse>> getSessions(
            @PathVariable UUID userId,
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(workoutSessionService.getSessions(userId, pageable));
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @GetMapping("/{sessionId}")
    public ResponseEntity<WorkoutSessionResponse> getSession(
            @PathVariable UUID userId,
            @PathVariable UUID sessionId) {
        return ResponseEntity.ok(workoutSessionService.getSession(userId, sessionId));
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @PutMapping("/{sessionId}")
    public ResponseEntity<WorkoutSessionResponse> updateSession(
            @PathVariable UUID userId,
            @PathVariable UUID sessionId,
            @RequestBody WorkoutSessionRequest request) {
        return ResponseEntity.ok(workoutSessionService.updateSession(userId, sessionId, request));
    }

    @PreAuthorize("hasRole('SUPERADMIN') or principal.id == #userId")
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable UUID userId,
            @PathVariable UUID sessionId) {
        workoutSessionService.deleteSession(userId, sessionId);
        return ResponseEntity.noContent().build();
    }
}
